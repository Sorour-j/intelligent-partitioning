package org.eclipse.epsilon.effectivemetamodel;

import org.eclipse.epsilon.eol.compile.m3.StructuralFeature;

public class EffectiveFeature{

	protected String name;
	protected int usage;
	protected EffectiveType effectiveType;
	protected String type;
	
	
	public EffectiveFeature(String name)
	{
		this.name = name;
		usage = 0;
	}
	
	public EffectiveType getEffectiveType() {
		return effectiveType;
	}
	
	public void setEffectiveType(EffectiveType effectiveType) {
		this.effectiveType = effectiveType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getUsage() {
		return usage;
	}
	
	public void increaseUsage()
	{
		usage++;
	}
	
	public boolean equals (EffectiveFeature f) {
		
		if (this.name.equals(f.name))
		
		{
			if (this.usage != f.usage)
			{
				return false;
			}
		}
			
		return true;
	}
	
}
