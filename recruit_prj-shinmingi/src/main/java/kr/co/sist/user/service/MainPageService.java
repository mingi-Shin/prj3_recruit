package kr.co.sist.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.sist.login.UserRepository;
import kr.co.sist.user.dto.JobPostDTO;
import kr.co.sist.user.entity.UserEntity;
import kr.co.sist.user.mapper.MainPageMapper;
import kr.co.sist.util.CipherUtil;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MainPageService {

    private final CipherUtil cu;
    private final UserRepository userRepository;
    private final MainPageMapper mainPageMapper;

    
    public UserEntity getUserInfo(String email) {
        return userRepository.findById(email).orElse(null);
    }
    
    
    //검색
    public List<JobPostDTO> searchJobPostings(String keyword){
    	
    	return mainPageMapper.searchJobPostings(keyword);
    }
    
    public List<String> getAutoCompleteSuggestions(String term){
    	 return mainPageMapper.getAutoCompleteSuggestions(term);
    }
}
