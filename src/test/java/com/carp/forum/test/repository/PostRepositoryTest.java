package com.carp.forum.test.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.carp.forum.entities.Board;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.Thread;
import com.carp.forum.repository.BoardRepository;
import com.carp.forum.repository.PostRepository;
import com.carp.forum.repository.ThreadRepository;

@SpringBootTest
public class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private BoardRepository boardRepository;

	static Board board;
	Thread thread;

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

	@Test
	public void createSinglePostTest() {
		Post post = new Post();
		post.setText("hello");
		post.setThread(thread);
		post = postRepository.save(post);
		Post fetchedPost = postRepository.findById(post.getId()).get();
		assertEquals(post, fetchedPost);
	}

	@Test
	public void createPostAndReplyTest() {
		Post post1 = new Post();
		post1.setText("1st post");
		post1.setThread(thread);
		post1 = postRepository.saveAndFlush(post1);

		Post post2 = new Post();
		post2.setText("2nd post, response");
		post2.setThread(thread);
		post2.addReplyTo(post1);
		post2 = postRepository.saveAndFlush(post2);

		post1 = postRepository.findById(post1.getId()).get();
	
		List<Post> posts = postRepository.findAll();

		assertEquals(post2.getReplyTo().size(), post1.getRepliedBy().size());
		assertTrue(post1.getRepliedBy().contains(post2));
		assertTrue(post2.getReplyTo().contains(post1));
	}
	
	@Test
	public void createPostAndMultipleRepliesTest() {
		Post post1 = new Post();
		post1.setText("1st post,get two replies");
		post1.setThread(thread);
		post1 = postRepository.saveAndFlush(post1);

		Post post2 = new Post();
		post2.setText("2nd post, is a response and gets a reply");
		post2.setThread(thread);
		post2.addReplyTo(post1);
		post2 = postRepository.saveAndFlush(post2);
		
		Post post3 = new Post();
		post3.setText("3rd post, double response");
		post3.setThread(thread);
		post3.addReplyTo(post1);
		post3.addReplyTo(post2);
		post3 = postRepository.saveAndFlush(post3);
		
		post1 = postRepository.findById(post1.getId()).get();
		post2 = postRepository.findById(post2.getId()).get();


		assertTrue(post1.getRepliedBy().containsAll(Arrays.asList(post3,post2)));
		assertTrue(post2.getRepliedBy().contains(post3));
		assertTrue(post2.getReplyTo().contains(post1));
		assertTrue(post3.getReplyTo().containsAll(Arrays.asList(post1,post2)));

	}

}
