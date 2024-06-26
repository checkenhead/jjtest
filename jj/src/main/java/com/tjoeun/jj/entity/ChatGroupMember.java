package com.tjoeun.jj.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatGroupMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer chatgroupid;
	private String nickname;
	
	public ChatGroupMember(Integer chatgroupid, String nickname) {
		this.chatgroupid = chatgroupid;
		this.nickname = nickname;
	}
}
