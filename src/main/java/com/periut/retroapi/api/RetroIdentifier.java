package com.periut.retroapi.api;

public record RetroIdentifier(String namespace, String path) {
	@Override
	public String toString() {
		return namespace + ":" + path;
	}
}
