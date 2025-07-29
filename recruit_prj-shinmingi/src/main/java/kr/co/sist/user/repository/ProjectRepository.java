package kr.co.sist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.sist.user.entity.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {

}
