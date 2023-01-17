package com.carp.forum.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import com.carp.forum.dto.PostDto;
import com.carp.forum.entities.Post;
import com.carp.forum.entities.User;

public class DtoTools {

	private static ModelMapper myMapper = new ModelMapper();
	static Converter<Set<Post>, Set<Long>> postToLongConverter = src -> src.getSource().stream().map(p -> p.getId()).collect(Collectors.toCollection(HashSet::new));
	//static Converter<Long,User> userToLongConverter = src -> src.getSource()==null ? null : new User()	;
	public static <TSource, TDestination> TDestination convert(TSource obj, Class<TDestination> clazz) {

		

		myMapper.typeMap(Post.class, PostDto.class).addMappings(mapper -> {
			
			mapper.using(postToLongConverter).map(src -> Optional.ofNullable(src.getReplyTo()).map(Collection::stream).orElse(Stream.empty()).map(p -> p.getId()).collect(Collectors.toCollection(HashSet::new)),PostDto::setReplyTo);
			
			mapper.using(postToLongConverter).map(src -> Optional.ofNullable(src.getRepliedBy()).map(Collection::stream).orElse(Stream.empty()).map(p -> p.getId()).collect(Collectors.toCollection(HashSet::new)),PostDto::setRepliedBy);

		});
		myMapper.typeMap(PostDto.class, Post.class).addMappings( mapper -> {
			//mapper.map(PostDto::getUserId,Post::setUser);
		});
		

		return myMapper.map(obj, clazz);
	}

}
