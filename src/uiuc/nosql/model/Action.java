package uiuc.nosql.model;

import java.io.Serializable;

public enum Action implements Serializable{
	Insert,
	Update,
	Get,
	Delete,
	ShowAll,
	Search
}
