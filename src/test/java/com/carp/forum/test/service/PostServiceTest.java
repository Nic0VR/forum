package com.carp.forum.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.Board;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.Thread;
import com.carp.forum.repository.BoardRepository;
import com.carp.forum.repository.PostRepository;
import com.carp.forum.repository.ThreadRepository;
import com.carp.forum.service.IPostService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

	@Autowired
	@InjectMocks
	private IPostService postService;
	@Mock
	private PostRepository postRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private BoardRepository boardRepository;

	private Board board;
	private Thread thread;
	
	
	@BeforeEach
	public void setUp() {
		board = new Board();
		board.setDescription("description");
		board.setTitle("test board");
		board.setRef("/test/");
		board = boardRepository.save(board);
		thread = new Thread();
		thread.setBoard(board);
		thread.setText("thread text test");
		thread.setTitle("thread title test");
		thread = threadRepository.save(thread);
		
	}
	
	@AfterEach()
	public void tearDown() {
		postRepository.deleteAll();
		threadRepository.deleteAll();
		boardRepository.deleteAll();
	}
	
	//@Test
	public void savePostTest() {
		Post post = new Post();
		post.setThread(thread);
		post.setText("post 1");
		post.setId(1);
		Post reponse1 = new Post();
		reponse1.setThread(thread);
		reponse1.setId(2);
		reponse1.setText("reponse 1");
		reponse1.addReplyTo(post);
		post.addRepliedBy(reponse1);
		
		PostDto postTest = new PostDto();
		
		when(postRepository.save(any())).thenReturn(board);
		
		
		postTest.setText("test 1");
		postTest.setThreadId(1);

		
	}
	
	
	
}
