/**
 * Copyright (c) SpaceToad, 2011 http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License
 * 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package buildcraft.factory.render;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.render.FluidRenderer;
import buildcraft.factory.TileTank;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class RenderTank extends TileEntitySpecialRenderer {

	ResourceLocation reslocation = new ResourceLocation("buildcraft:textures/blocks/tank_sleeve.png");
	
	public class SleeveBase extends ModelBase
	{		
		public ModelRenderer sleevemodel;
		
		public SleeveBase()
		{			
			sleevemodel = new ModelRenderer(this, 0, 0); // same size sleeve as pipe
			//sleevemodel = new ModelRenderer(this, 0, 16); // larger sleeve than pipe
			
			sleevemodel.setTextureSize(32,32);
			
			sleevemodel.addBox(-4, -4, -8, 8, 8, 2); // same size sleeve as pipe
			//sleevemodel.addBox(-5, -5, -8, 10, 10, 2); // larger sleeve than pipe
		}
		
		public void renderModel(TileEntity te, float f1)
		{
			
			func_110628_a(reslocation);	
			
			TileEntity adjacent_te = te.worldObj.getBlockTileEntity(te.xCoord,  te.yCoord,  te.zCoord + 1);
			if (adjacent_te instanceof IPipeTile && ((IPipeConnection)adjacent_te).isPipeConnected(ForgeDirection.NORTH))
			{		
				sleevemodel.rotateAngleY = 0;	// south	
				sleevemodel.render(f1);
			}
			
			adjacent_te = te.worldObj.getBlockTileEntity(te.xCoord - 1,  te.yCoord,  te.zCoord);
			if (adjacent_te instanceof IPipeTile && ((IPipeConnection)adjacent_te).isPipeConnected(ForgeDirection.EAST))
			{		
				sleevemodel.rotateAngleY = 90.0F * ((float)Math.PI / 180.0F);  // west
				sleevemodel.render(f1);
			}
			
			adjacent_te = te.worldObj.getBlockTileEntity(te.xCoord,  te.yCoord,  te.zCoord - 1);
			if (adjacent_te instanceof IPipeTile && ((IPipeConnection)adjacent_te).isPipeConnected(ForgeDirection.SOUTH))
			{		
				sleevemodel.rotateAngleY = 180.0F * ((float)Math.PI / 180.0F);  // north
				sleevemodel.render(f1);
			}
			
			adjacent_te = te.worldObj.getBlockTileEntity(te.xCoord + 1,  te.yCoord,  te.zCoord);
			if (adjacent_te instanceof IPipeTile && ((IPipeConnection)adjacent_te).isPipeConnected(ForgeDirection.WEST))
			{		
				sleevemodel.rotateAngleY = 270.0F * ((float)Math.PI / 180.0F);  // east
				sleevemodel.render(f1);
			}
		}
	}
	
	SleeveBase sleeves = new SleeveBase();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {		
		
		// -- Render Pipe Sleeves -------------------
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glTranslatef((float) x, (float) y + 1, (float) z + 1);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		sleeves.renderModel(tileentity, 0.0625F);		
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		
		// -------------------------------------------
		
		TileTank tank = ((TileTank) tileentity);

		FluidStack liquid = tank.tank.getFluid();
		if (liquid == null || liquid.amount <= 0) {
			return;
		}

		int[] displayList = FluidRenderer.getFluidDisplayLists(liquid, tileentity.worldObj, false);
		if (displayList == null) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		func_110628_a(FluidRenderer.getFluidSheet(liquid));

		GL11.glTranslatef((float) x + 0.125F, (float) y, (float) z + 0.125F);
		GL11.glScalef(0.75F, 0.999F, 0.75F);

		GL11.glCallList(displayList[(int) ((float) liquid.amount / (float) (tank.tank.getCapacity()) * (FluidRenderer.DISPLAY_STAGES - 1))]);

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
