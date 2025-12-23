package com.back.service;

import com.back.entity.Member;
import com.back.entity.Post;
import com.back.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(Member author, String title, String content) {
        Post post = new Post(author, title, content);
        author.increaseActivityScore(3);// 글작성하면 3점
        return postRepository.save(post);
    }

    public Optional<Post> findById(Integer id){
        return postRepository.findById(id);
    }
}