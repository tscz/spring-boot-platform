package com.github.tscz.spring.platform.testentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "testEntity")
public class TestEntity {
	@Id
	@GeneratedValue(generator = "testEntity_generator")
	@SequenceGenerator(name = "testEntity_generator", sequenceName = "testEntity_sequence", initialValue = 1000)
	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotBlank
	@Size(min = 3, max = 100)
	private String title;

	@Column(columnDefinition = "text")
	private String description;

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Testentity [id=" + id + ", title=" + title + ", description=" + description + "]";
	}

}
