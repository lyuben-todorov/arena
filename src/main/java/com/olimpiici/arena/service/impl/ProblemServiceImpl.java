package com.olimpiici.arena.service.impl;

import com.olimpiici.arena.service.ProblemService;
import com.olimpiici.arena.service.TagService;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;
import com.olimpiici.arena.config.ApplicationProperties;
import com.olimpiici.arena.domain.Problem;
import com.olimpiici.arena.domain.TagCollection;
import com.olimpiici.arena.repository.ProblemRepository;
import com.olimpiici.arena.repository.TagCollectionRepository;
import com.olimpiici.arena.repository.TagCollectionTagRepository;
import com.olimpiici.arena.repository.TagRepository;
import com.olimpiici.arena.service.dto.ProblemDTO;
import com.olimpiici.arena.service.dto.TagDTO;
import com.olimpiici.arena.service.mapper.ProblemMapper;
import com.olimpiici.arena.service.mapper.TagMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Problem.
 */
@Service
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final Logger log = LoggerFactory.getLogger(ProblemServiceImpl.class);

    private final ProblemRepository problemRepository;

    private final ProblemMapper problemMapper;
    
    private final TagCollectionTagRepository tagCollectionTagRepository;
    
    private final TagCollectionRepository tagCollectionRepository;

    private final TagRepository tagRepository;
    
    private final TagMapper tagMapper;
    
    private final TagService tagService;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    public ProblemServiceImpl(ProblemRepository problemRepository, 
    		ProblemMapper problemMapper, 
    		TagCollectionTagRepository tagCollectionTagRepository,
    		TagCollectionRepository tagCollectionRepository,
    		TagRepository tagRepository,
    		TagMapper tagMapper,
    		TagService tagService) {
        this.problemRepository = problemRepository;
        this.problemMapper = problemMapper;
        this.tagCollectionTagRepository = tagCollectionTagRepository;
        this.tagCollectionRepository = tagCollectionRepository;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.tagService = tagService;
    }

    /**
     * Save a problem.
     *
     * @param problemDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProblemDTO save(ProblemDTO problemDTO) {
        log.debug("Request to save Problem : {}", problemDTO);

        Problem problem = problemMapper.toEntity(problemDTO);
        problem = problemRepository.save(problem);
        return problemMapper.toDto(problem);
    }

    /**
     * Get all the problems.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProblemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Problems");
        return problemRepository.findAll(pageable)
            .map(problemMapper::toDto);
    }


    /**
     * Get one problem by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProblemDTO> findOne(Long id) {
        log.debug("Request to get Problem : {}", id);
        return problemRepository.findById(id)
            .map(problemMapper::toDto);
    }

    /**
     * Delete the problem by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Problem : {}", id);
        problemRepository.deleteById(id);
    }
    
    @Override
    public List<TagDTO> findTags(Long id) {
    	Problem problem = problemRepository.getOne(id);
    	return tagService.findTagsForCollection(problem.getTags())
	    	.map(tagMapper::toDto)
			.collect(Collectors.toList());
    }
    
    @Override
    public void updateTags(Long id, List<TagDTO> newTags) {
    	Problem problem = problemRepository.getOne(id);
    	TagCollection newCollection	= 
    			tagService.updateTagsForCollection(problem.getTags(), newTags);
    	
    	if (problem.getTags() == null) {
	    	problem.setTags(newCollection);
			problemRepository.save(problem);
    	}
    }

    @Override
	public Properties getProperties(Long problemId) throws Exception {
    	Properties props = new Properties();
		File gradePropertiesFile = getGradeProperties(problemId);
		try (FileInputStream fis = new FileInputStream(gradePropertiesFile)) {
			props.load(fis);
		}
		return props;
	}
    
	@Override
	public void updateTimeLimit(Long problemId, int newTimeLimitMs) throws Exception {
		String timeValue = newTimeLimitMs/1000 + "." + newTimeLimitMs%1000;
		
		Properties props = getProperties(problemId);
		props.setProperty("time", timeValue);
		writeGradeProperties(problemId, props);
		recreateProblemZip(problemId);
	}

	@Override
	public void updateMemoryLimit(Long problemId, int newMemoryLimitMb) throws Exception {
		Properties props = getProperties(problemId);
		props.setProperty("memory", String.valueOf(newMemoryLimitMb));
		writeGradeProperties(problemId, props);
		recreateProblemZip(problemId);
	}

	private File getGradeProperties(long problemId) {
		return Paths.get(applicationProperties.getWorkDir(), "problems", String.valueOf(problemId), "problem", "grade.properties")
				.toFile();
	}
	
	private void writeGradeProperties(long problemId, Properties props) throws Exception {
		File gradePropertiesFile = getGradeProperties(problemId);
		try (PrintWriter pw = new PrintWriter(gradePropertiesFile)) {
			props.store(pw, null);
		}
	}
	
	private void recreateProblemZip(long problemId) throws Exception {
		ProcessExecutor executor = new ProcessExecutor()
      			.command("zip", "-r", "problem.zip", ".")
      			.directory(Paths.get(applicationProperties.getWorkDir(), "problems", String.valueOf(problemId), "problem").toFile());
      	executor.execute();
      	
      	File problemZipNew = Paths.get(applicationProperties.getWorkDir(), "problems", String.valueOf(problemId), "problem", "problem.zip").toFile();
      	File problemZipOrig = Paths.get(applicationProperties.getWorkDir(), "problems", String.valueOf(problemId), "problem.zip").toFile();
      	problemZipOrig.delete();
      	problemZipNew.renameTo(problemZipOrig);
	}

	@Override
	public ProblemDTO setLimitsToDto(ProblemDTO dto) {
		int time = 1000;
		int memory = 256;
		
		File propertyFile = getGradeProperties(dto.getId());
		if (propertyFile.exists()) {
       		try (InputStream is = new FileInputStream(propertyFile)) {
       			Properties props = new Properties();
	        	props.load(is);
	        			
	        	time = (int) (1000*Double.valueOf(props.getProperty("time", "1")) + 0.1);
	        	memory = Integer.valueOf(props.getProperty("memory", ""+memory));
	        } catch (Exception e) {
	        	log.error("Cannot read metadata for problem: " + dto.getId(), e);
	        }
		}
		
        dto.setTime(time);
        dto.setMemory(memory);
        return dto;
	}
}
