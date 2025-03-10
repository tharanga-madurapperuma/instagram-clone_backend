package com.example.backend.service;

import com.example.backend.model.Post;
import com.example.backend.model.DatabaseSequence;
import com.example.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post getPostById(String id){
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post addPost(Post post){
        post.setPostId(generateSequence(Post.SEQUENCE_NAME));
        return postRepository.save(post);
    }

    public Post updatePost(Post post, String id){
        Post existingPost = postRepository.findById(post.getPostId()).orElseThrow(
                () -> new RuntimeException("Post not found")
        );

        existingPost.setUserId(post.getUserId() != null && !post.getUserId().isEmpty() ? post.getUserId() : existingPost.getUserId());
        existingPost.setImageUrl(post.getImageUrl() != null && !post.getImageUrl().isEmpty() ? post.getImageUrl() : existingPost.getImageUrl());
        existingPost.setDescription(post.getDescription() != null && !post.getDescription().isEmpty() ? post.getDescription() : existingPost.getDescription());

        return existingPost;
    }

    public void deletePost(String id){
        postRepository.findById(id).orElseThrow(
            () -> new RuntimeException(id)
        );

        postRepository.deleteById(id);
    }

    public String incrementLikes(String postId){
        Post post = postRepository.findById(postId).orElseThrow(
            () -> new RuntimeException(postId)
        );

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
        return "Like Count Increased";
    }

    public String decrementLikes(String postId){
        Post post = postRepository.findById(postId).orElseThrow(
            () -> new RuntimeException(postId)
        );

        int likeCount = 0;
        if(post.getLikeCount() == 0){
            likeCount = 0;
        } 
        else{
            likeCount = post.getLikeCount() - 1;
        }

        post.setLikeCount(likeCount);
        postRepository.save(post);
        return "Like Count Decreased";
    }

    public Integer getLikeCount(String postId){
        Post post = postRepository.findById(postId).orElseThrow(
            () -> new RuntimeException(postId)
        );

        return post.getLikeCount();
    }

    public String editPost(String id, Post post){
        Post existingPost = postRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Post not found")
        );

        existingPost.setUserId(post.getUserId() != null && !post.getUserId().isEmpty() ? post.getUserId() : existingPost.getUserId());
        existingPost.setImageUrl(post.getImageUrl() != null && !post.getImageUrl().isEmpty() ? post.getImageUrl() : existingPost.getImageUrl());
        existingPost.setDescription(post.getDescription() != null && !post.getDescription().isEmpty() ? post.getDescription() : existingPost.getDescription());

        postRepository.save(existingPost);
        return "Post updated successfully";

    }

    public List<Post> getPostIdByUserId(String userId){
        return postRepository.findPostIdByUserId(userId);
    }


    @Autowired
    private MongoOperations mongoOperations;

    public String generateSequence(String seqName){
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return "P" + (!Objects.isNull(counter) ? counter.getSeq() : 1);
    }
}
