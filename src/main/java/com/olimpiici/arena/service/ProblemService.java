package com.olimpiici.arena.service;

import com.olimpiici.arena.service.dto.ProblemDTO;
import com.olimpiici.arena.service.dto.TagDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Service Interface for managing Problem.
 */
public interface ProblemService {

    /**
     * Save a problem.
     *
     * @param problemDTO the entity to save
     * @return the persisted entity
     */
    ProblemDTO save(ProblemDTO problemDTO);

    /**
     * Get all the problems.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProblemDTO> findAll(Pageable pageable);


    /**
     * Get the "id" problem.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ProblemDTO> findOne(Long id);

    /**
     * Delete the "id" problem.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    public List<TagDTO> findTags(Long id);

	void updateTags(Long id, List<TagDTO> newTags);
	
    // Automaticaly sets the time limits for all problems
    void autoSetTimeLimits() throws Exception;

    void autoSetTimeLimits(Long idp) throws Exception;

    void updateTimeLimit(Long problemId, int newTimeLimitMs) throws Exception;

    void updateMemoryLimit(Long problemId, int newMemoryLimitMb) throws Exception;

    Properties getProperties(Long problemId) throws Exception;

    ProblemDTO setLimitsToDto(ProblemDTO dto);
}
