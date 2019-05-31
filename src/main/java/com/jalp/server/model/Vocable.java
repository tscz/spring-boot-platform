package com.jalp.server.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vocabulary")
public class Vocable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column
	private String value;

	@Column
	private String translation;

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	@Override
	public String toString() {
		return "Vocable [id=" + id + ", value=" + value + ", translation=" + translation + "]";
	}
}
