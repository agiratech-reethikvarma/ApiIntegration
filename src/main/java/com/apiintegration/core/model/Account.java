package com.apiintegration.core.model;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ToString.Exclude
	@JsonBackReference
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String accountName;

	@JsonManagedReference
	@ToString.Exclude
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Set<Project> projects = new LinkedHashSet<>();

	@Column(nullable = false)
	private String accountDescription;

	private Long usersCount;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	@Version
	private Long version;

	public void increasUsersCount() {
		this.usersCount = usersCount++;
	}

	public void decreaseUsersCount() {
		this.usersCount = usersCount--;
	}
}