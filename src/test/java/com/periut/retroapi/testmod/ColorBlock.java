package com.periut.retroapi.testmod;

import com.periut.retroapi.api.RetroBlock;
import com.periut.retroapi.api.RetroIdentifier;
import com.periut.retroapi.api.RetroTexture;
import com.periut.retroapi.api.RetroTextures;
import net.minecraft.block.material.Material;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ColorBlock extends RetroBlock {
	private final RetroTexture[] faceTextures = new RetroTexture[6];

	public ColorBlock(Material material) {
		super(material);
		faceTextures[0] = RetroTextures.addBlockTexture(new RetroIdentifier("retroapi_test", "color_block_bottom"));
		faceTextures[1] = RetroTextures.addBlockTexture(new RetroIdentifier("retroapi_test", "color_block_top"));
		faceTextures[2] = RetroTextures.addBlockTexture(new RetroIdentifier("retroapi_test", "color_block_north"));
		faceTextures[3] = RetroTextures.addBlockTexture(new RetroIdentifier("retroapi_test", "color_block_south"));
		faceTextures[4] = RetroTextures.addBlockTexture(new RetroIdentifier("retroapi_test", "color_block_west"));
		faceTextures[5] = RetroTextures.addBlockTexture(new RetroIdentifier("retroapi_test", "color_block_east"));
		setSprite(faceTextures[0].id);
		RetroTextures.trackBlock(this, faceTextures[0]);
	}

	@Override
	public int getSprite(int face) {
		if (face >= 0 && face < faceTextures.length) {
			return faceTextures[face].id;
		}
		return super.getSprite(face);
	}
}
