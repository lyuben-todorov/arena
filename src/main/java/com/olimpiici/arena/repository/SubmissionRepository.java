package com.olimpiici.arena.repository;

import com.olimpiici.arena.domain.CompetitionProblem;
import com.olimpiici.arena.domain.Submission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Submission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("select submission from Submission submission where submission.user.login = ?#{principal.username}")
    List<Submission> findByUserIsCurrentUser();
    
    Page<Submission> findByCompetitionProblem(CompetitionProblem competitionProblem, Pageable pageable);
}
