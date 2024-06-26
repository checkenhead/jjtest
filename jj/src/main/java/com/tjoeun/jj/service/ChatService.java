package com.tjoeun.jj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tjoeun.jj.dao.ChatGroupMemberRepository;
import com.tjoeun.jj.dao.ChatGroupRepository;
import com.tjoeun.jj.dao.ChatRepository;
import com.tjoeun.jj.dao.MemberRepository;
import com.tjoeun.jj.dto.ChatGroupDto;
import com.tjoeun.jj.entity.Chat;
import com.tjoeun.jj.entity.ChatGroup;
import com.tjoeun.jj.entity.ChatGroupMember;
import com.tjoeun.jj.entity.Member;

@Service
@Transactional
public class ChatService {
	
	@Autowired
	ChatRepository cr;
	
	@Autowired
	ChatGroupRepository cgr;
	
	@Autowired
	ChatGroupMemberRepository cgmr;
	
	@Autowired
	MemberRepository mr;

	public Chat insertChat(Chat chat) {
		return cr.save(chat);
	}

//	public List<Chat> getAllChatBySenderAndReceiver(Chat chat) {
//		
//		return cr.getAllChatBySenderAndReceiver(chat.getSender(),chat.getReceiver());
//	}

	public List<Chat> getNewChat(Chat chat) {
		return cr.getNewChat(chat.getChatgroupid(), chat.getId());
	}

	public List<ChatGroupDto> getAllChatGroupsByNickname(String nickname) {
		List<ChatGroupDto> groups= new ArrayList<ChatGroupDto>();
		List<ChatGroup> result = cgr.findAllByNickname(nickname);
		
		for(ChatGroup cgdto : result) {
			groups.add(new ChatGroupDto(cgdto.getId(), cgdto.getCreatedby(), cgdto.getMembercount(), null));
		}

		return groups;
	}

	public List<Member> getMemberByChatgroupid(Integer chatgroupid) {
		return mr.findAllByChatgroupidInChatGroupMember(chatgroupid);
	}

	public ChatGroupDto createGroup(List<String> members) {
		// 1. 1:1 채팅일 경우
		if(members.size() == 2) {
			ChatGroup cg = null;
			
			// 1-1. 내가 만든 그룹 중 상대방이 포함된 그룹이 있는지, 있다면 해당 chatgroupid return
			cg = cgr.findGroupByTwoMembers(members.get(0), members.get(1));
			
			if(cg != null) {
				return new ChatGroupDto(cg.getId(), cg.getCreatedby(), cg.getMembercount(), null);
			}
//				return chatgroupid;
			
			// 1-2. 상대방이 만든 그룹 중 내가 포함된 그룹이 있는지, 있다면 해당 chatgroupid return
			cg = cgr.findGroupByTwoMembers(members.get(1), members.get(0));
			
			if(cg != null) {
				return new ChatGroupDto(cg.getId(), cg.getCreatedby(), cg.getMembercount(), null);
			}
//				return chatgroupid;
			
			// 1-3. 없다면 새 그룹 create, 해당 chatgroupid return
			cg = cgr.save(new ChatGroup(members.get(0), 2));
			
			for(String nickname : members) {
				cgmr.save(new ChatGroupMember(cg.getId(), nickname));				
			}
			
			return new ChatGroupDto(cg.getId(), cg.getCreatedby(), cg.getMembercount(), null);
			
		// 2. 1:다 채팅일 경우
		}else if(members.size() > 2) {
			
			ChatGroup cg = null;
			
			cg = cgr.save(new ChatGroup(members.get(0), members.size()));
			
			for(String nickname : members) {
				cgmr.save(new ChatGroupMember(cg.getId(), nickname));				
			}
			
			return new ChatGroupDto(cg.getId(), cg.getCreatedby(), cg.getMembercount(), null);
		}
		
		return null;
	}
	
	public void inviteGroup(Integer chatgroupid, List<String> members) {
		for(String nickname : members) {
			cgmr.save(new ChatGroupMember(chatgroupid, nickname));				
		}
	}

	public void leaveGroup(Integer chatgroupid, String nickname) {
		// ChatGroupMember 레코드 삭제
		Optional<ChatGroupMember> optionalCgm = cgmr.findByChatgroupidAndNickname(chatgroupid, nickname);
		cgmr.delete(optionalCgm.get());
		
		
		ChatGroup cgdto = cgr.findById(chatgroupid).get();
		
		if(cgdto.getMembercount() == 1) {
			// membercount가 1이면 삭제(본인 혼자 있던 그룹이면)
			cgr.delete(cgdto);
			return;
		}else if(cgdto.getCreatedby().equals(nickname)){
			// createdby가 본인이면 다른 사람에게 위임
			List<ChatGroupMember> cgmdto = cgmr.findAllByChatgroupid(chatgroupid);
			cgdto.setCreatedby(cgmdto.get(0).getNickname());
		}
		// membercount - 1,save
		cgdto.setMembercount(cgdto.getMembercount() - 1);
		cgr.save(cgdto);
	}

	





}
