package com.tjoeun.jj.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tjoeun.jj.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {

	Optional<Member> findByNickname(String nickname);

	@Query("select m from Member m where nickname like :keyword")
	List<Member> findByKeyword(@Param("keyword") String keyword);

	@Query("select m from Member m where nickname in (select cgm.nickname from ChatGroupMember cgm where cgm.chatgroupid = :chatgroupid)")
	List<Member> findAllByChatgroupidInChatGroupMember(@Param("chatgroupid") Integer chatgroupid);

	@Query("SELECT f.following FROM Follow f WHERE f.follower IN "
			+ "(SELECT m.nickname FROM Member m WHERE m.nickname IN "
			+ "	(SELECT f.follower FROM Follow f WHERE f.following = :nickname) OR m.nickname IN "
			+ "	(SELECT f.following FROM Follow f WHERE f.follower = :nickname)"
			+ ") and f.following not in (:nickname)")
	List<String> findRecommendPeopleByNickname(@Param("nickname") String nickname);

	@Query("select m from Member m where m.email = :email")
	Optional<Member> getWithRolesById(@Param("email") String email);

	@Query("select m from Member m where m.nickname in "
			+ "(select l.nickname from Likes l where l.feedid = :feedid) "
			+ " and m.nickname not in (:nickname) order by rand() desc limit 5")
	List<Member> findRecommendPeopleByFeedid(@Param("nickname") String nickname, @Param("feedid") String feedid);
	
	@Query("select m from Member m where m.nickname not in"
			+ " (select f.following from Follow f where f.follower = :nickname ) "
			+ "	and m.nickname not in (:nickname) "
			+ " order by rand() desc limit 5")
	List<Member> findRandomMember(@Param("nickname") String nickname);

}
