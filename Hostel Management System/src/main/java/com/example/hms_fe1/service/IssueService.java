package com.example.hms_fe1.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hms_fe1.entity.IssueEntity;
import com.example.hms_fe1.repository.IssueRepository;

@Service
public class IssueService {
	
	@Autowired
	private IssueRepository issueRepository;
	
	public void saveIssue(IssueEntity issue) {
		issueRepository.save(issue);
	}
	
	public Set<IssueEntity> getAllIssues(){
		List<IssueEntity> allIssues = issueRepository.findAll();
		Set<IssueEntity> issues = new HashSet<IssueEntity>(allIssues);	
		return issues;
	}
	
	public Set<IssueEntity> getIssuesOfUser(String id){
		List<IssueEntity> allIssues = issueRepository.findAll();
		Set<IssueEntity> userissues = new HashSet<IssueEntity>();
		for(IssueEntity issue: allIssues) {
			if(issue.getStudentId().equals(id))
				userissues.add(issue);
		}
		return userissues;
	}
	
	public IssueEntity getIssueById(int id) {
		return issueRepository.getById(id);
	}
	
	public int pendingIssuesCount() {
		return issueRepository.pendingIssuesCount();
	}
	

}
