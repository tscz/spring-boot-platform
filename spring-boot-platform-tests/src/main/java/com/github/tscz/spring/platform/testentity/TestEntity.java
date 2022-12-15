package com.github.tscz.spring.platform.testentity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

	@Override
	public int hashCode() {
		return Objects.hash(description, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestEntity other = (TestEntity) obj;
		return Objects.equals(description, other.description) && Objects.equals(title, other.title);
	}

}
