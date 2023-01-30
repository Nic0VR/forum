package com.carp.forum.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.carp.forum.entities.Thread;
import com.carp.forum.entities.Board;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.User;
import com.carp.forum.enums.Role;
import com.carp.forum.repository.BoardRepository;
import com.carp.forum.repository.PostRepository;
import com.carp.forum.repository.ThreadRepository;
import com.carp.forum.repository.UserRepository;

@Component
public class DataInitializer implements ApplicationRunner {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private PostRepository postRepository;
	
	
	public DataInitializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(userRepository.count()==0) {
			User admin = new User();
			admin.setRole(Role.ADMIN);
			admin.setUsername("admin");
			admin.setPassword(HashTools.hashSHA512("admin"));
			admin.setEmail("admin@mail.fr");
			admin = userRepository.save(admin);
			
			User user = new User();
			user.setRole(Role.USER);
			user.setUsername("user");
			user.setPassword(HashTools.hashSHA512("user"));
			user.setEmail("user@mail.fr");
			user = userRepository.save(user);
			
		}
		if(boardRepository.count()==0 && threadRepository.count()==0 && postRepository.count()==0) {
			Board board = new Board();
			board.setTitle("test board");
			board.setRef("test");
			board.setDescription("this is the test board");
			board = boardRepository.save(board);
			
//			Thread thread = new Thread();
//			thread.setBoard(board);
//			thread.setText("1st thread test");
//			thread.setTitle("test thread 1");
//			thread = threadRepository.save(thread);
//			
//			Post post1 = new Post();
//			post1.setThread(thread);
//			post1.setText("post 1");
//			post1 = postRepository.save(post1);
		}

	}

}
