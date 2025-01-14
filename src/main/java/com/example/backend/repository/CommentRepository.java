package com.example.backend.repository;

import com.example.backend.model.Comment;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
    public List<Comment> getCommentByPostId(String postId);
}
