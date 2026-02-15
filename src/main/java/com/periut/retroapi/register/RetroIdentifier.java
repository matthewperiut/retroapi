package com.periut.retroapi.register;

public record RetroIdentifier(String namespace, String path) {
	@Override
	public String toString() {
		return namespace + ":" + path;
	}
}
