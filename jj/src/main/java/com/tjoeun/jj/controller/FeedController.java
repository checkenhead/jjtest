package com.tjoeun.jj.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.tjoeun.jj.dto.PostDto;
import com.tjoeun.jj.entity.Bookmarks;
import com.tjoeun.jj.entity.Feed;
import com.tjoeun.jj.entity.Likes;
import com.tjoeun.jj.entity.Reply;
import com.tjoeun.jj.entity.ReplyView;
import com.tjoeun.jj.service.FeedService;
import com.tjoeun.jj.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController {

	@Autowired
	FeedService fs;

	@Autowired
	MemberService ms;

	@PostMapping("/post")
	public HashMap<String, Object> postFeed(@RequestBody PostDto post) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		Feed feed = fs.postFeed(post);

		if (feed == null) {
			result.put("message", "Error");
		} else {

			fs.insertMention(feed.getId(), post.getContent());
			fs.insertHashTag(feed.getId(), post.getContent());

			result.put("message", "OK");
			result.put("feed", feed);

		}

		return result;
	}

	@PostMapping("/getallfeeds")
	public HashMap<String, Object> getAllFeeds(@RequestParam("page") int page) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		PageRequest pageRequest = PageRequest.of(page, 3);

		result.put("feeds", fs.getAllFeeds(pageRequest));

		return result;
	}

	@PostMapping("/getbookmarkfeedsbynickname")
	public HashMap<String, Object> getBookmarkfeedsBynickname(@RequestParam("page") int page,
			@RequestParam("nickname") String nickname) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		PageRequest pageRequest = PageRequest.of(page, 3);

		result.put("feeds", fs.getBookmarkfeedsBynickname(pageRequest, nickname));

		return result;
	}

	@PostMapping("/getfeedimgbyfeedid")
	public HashMap<String, Object> getFeedimgByFeedid(@RequestParam("feedid") Integer feedid) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("images", fs.getFeedimgByFeedid(feedid));

		return result;
	}

	@PostMapping("/getsummaryview")
	public HashMap<String, Object> getSummaryView(@RequestParam("nickname") String nickname) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("summarys", fs.getSummarysByNickname(nickname));

		return result;
	}

	@PostMapping("/getsummarymentions")
	public HashMap<String, Object> getSummaryMentions(@RequestParam("nickname") String nickname) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("summarys", fs.getMentionsByNickname(nickname));

		return result;
	}

	@PostMapping("/deletebyid")
	public HashMap<String, Object> deleteById(@RequestBody Feed feed) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		fs.deleteFeed(feed);

		return result;
	}

	@PostMapping("/getlikesbyfeedid")
	public HashMap<String, Object> getLikeByFeedid(@RequestBody Likes likes) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("likes", fs.getLikesByFeedid(likes));

		return result;
	}

	@PostMapping("/togglelike")
	public void toggleLike(@RequestBody Likes likes) {
		fs.toggleLike(likes);
	}

	@PostMapping("/getreplysbyfeedid")
	public HashMap<String, Object> getReplysByFeedid(@RequestBody ReplyView replyview) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("replys", fs.getReplysByFeedid(replyview));

		return result;
	}

	@PostMapping("/addreply")
	public void addReply(@RequestBody Reply reply) {
		fs.insertReply(reply);
	}

	@PostMapping("/deletereply")
	public void deleteReply(@RequestParam("id") Integer id) {
		fs.deleteReply(id);
	}

	@PostMapping("/getbookmarksbyfeedid")
	public HashMap<String, Object> getBookmarksByFeedid(@RequestBody Bookmarks bookmark) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("bookmarks", fs.getBookmarksByFeedid(bookmark));

		return result;
	}

	@PostMapping("/togglebookmark")
	public void toggleBookmark(@RequestBody Bookmarks bookmark) {
		fs.toggleBookmark(bookmark);
	}

	@PostMapping("/getfeedbyid")
	public HashMap<String, Object> getFeedById(@RequestBody Feed feed) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("feed", fs.getFeedById(feed));

		return result;
	}

	@PostMapping("/getFeedByKeyword")
	public HashMap<String, Object> getFeedByKeyword(@RequestParam("keyword") String keyword) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("feeds", fs.getFeedByKeyword(keyword));

		return result;
	}

	@PostMapping("/getfollowingfeeds")
	public HashMap<String, Object> getFollowingFeeds(@RequestParam("page") int page,
			@RequestParam("nickname") String nickname) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		PageRequest pageRequest = PageRequest.of(page, 3);

		result.put("feeds", fs.getFollowingFeeds(pageRequest, nickname));

		return result;
	}

	@PostMapping("/getrecommendfeedsbynickname")
	public HashMap<String, Object> getRecommendFeedsByNickname(@RequestParam("nickname") String nickname) {
		HashMap<String, Object> result = new HashMap<String, Object>();

		List<Feed> list = fs.getRecommendFeedsByNickname(nickname);

		result.put("recommendfeeds", list);

		return result;
	}

	@PostMapping("/getrandomfeed")
	public HashMap<String, Object> getRandomfeed(@RequestParam("nickname") String nickname) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("feeds", fs.getRandomFeed(nickname));
		
		return result;
	}
}
