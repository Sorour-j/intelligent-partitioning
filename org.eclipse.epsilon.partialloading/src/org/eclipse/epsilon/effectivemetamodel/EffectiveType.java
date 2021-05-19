package org.eclipse.epsilon.effectivemetamodel;

import java.util.ArrayList;

import org.eclipse.epsilon.eol.compile.m3.StructuralFeature;
import org.eclipse.epsilon.eol.execute.Return;


public class EffectiveType {

	protected String name;
	protected XMIN effectiveMetamodel;
	protected ArrayList<EffectiveFeature> attributes = new ArrayList<EffectiveFeature>();
	protected ArrayList<EffectiveFeature> references = new ArrayList<EffectiveFeature>();
	protected ArrayList<StructuralFeature> traversalAttributes = new ArrayList<StructuralFeature>();
	protected ArrayList<StructuralFeature> traversalReferences = new ArrayList<StructuralFeature>();
	protected int usage = 1;
	
	public boolean isequals(EffectiveType t) {
		int index = 0;
		
		if (this.name.equals(t.name)) {
			
			if (this.effectiveMetamodel.getName().equals(t.effectiveMetamodel.getName())) {
			
				for(EffectiveFeature feature : this.attributes) {
					if (!t.getAttributes().isEmpty()) {
					
						if (! feature.equals(t.getAttributes().get(index))) {
						return false;
						}
						index++;
					}
					else
						return false;
				}
				index = 0;
				for(EffectiveFeature ref : this.references) {
					if (!t.getReferences().isEmpty()) {
						if (! ref.equals(t.getReferences().get(index))) {
							return false;
					}
						index++;
					}
					else
						return false;
				}
			}
			return true;
					
		}
		else
				return false;
		
	}
	
	public EffectiveType(String name)
	{
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public XMIN getEffectiveMetamodel() {
		return effectiveMetamodel;
	}
	
	public void setEffectiveMetamodel(XMIN effectiveMetamodel) {
		this.effectiveMetamodel = effectiveMetamodel;
	}
	
	public ArrayList<EffectiveFeature> getAttributes() {
		return attributes;
	}
	
	public ArrayList<EffectiveFeature> getReferences() {
		return references;
	}
	
	public boolean containsAttribute(String attribute)
	{
		for(EffectiveFeature ef: attributes)
		{
			if (ef.getName().equals(attribute)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsReference(String reference)
	{
		for(EffectiveFeature ef: references)
		{
			if (ef.getName().equals(reference)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsFeature(String feature)
	{
		return containsAttribute(feature) || containsReference(feature);
	}
	
	public EffectiveFeature addToAttributes(String attribute)
	{
		for(EffectiveFeature ef: attributes)
		{
			if (ef.getName().equals(attribute)) {
				ef.setEffectiveType(this);
				//ef.increaseUsage();
				return ef;
			}
		}
		
		EffectiveFeature attr = new EffectiveFeature(attribute);
		attributes.add(attr);
		attr.setEffectiveType(this);
		return attr;
	}
	
	public EffectiveFeature addToReferences(String reference)
	{
		for(EffectiveFeature ef: references)
		{
			if(ef.getName().equals(reference))
			{
				//ef.increaseUsage();
				return ef;
			}
		}
		EffectiveFeature ref = new EffectiveFeature(reference);
		references.add(ref);
		ref.setEffectiveType(this);
		return ref;
	}
	public void addToTraversalReferences(StructuralFeature ref)
	{
		if (!traversalReferences.contains(ref))
			traversalReferences.add(ref);
	}
	public void addToTraversalAttributes(StructuralFeature attr)
	{
		if (!traversalAttributes.contains(attr))
			traversalAttributes.add(attr);
	}
	public ArrayList<StructuralFeature> getTraversalAttributes() {
		return traversalAttributes;
	}
	public ArrayList<StructuralFeature> getTraversalReferences() {
		return traversalReferences;
	}
	
	public EffectiveFeature increaseAttributeUsage(String attribute)
	{
		for(EffectiveFeature ef: attributes)
		{
			if (ef.getName().equals(attribute)) {
				//ef.increaseUsage();
				return ef;
			}
		}
		return null;
	}
	
	public EffectiveFeature increaseReferenceUsage(String reference)
	{
		for(EffectiveFeature ef: references)
		{
			if (ef.getName().equals(reference)) {
				ef.increaseUsage();
				return ef;
			}
		}
		return null;
	}
	
	public ArrayList<EffectiveFeature> getAllFeatures()
	{
		ArrayList<EffectiveFeature> result = new ArrayList<EffectiveFeature>();
		result.addAll(attributes);
		result.addAll(references);
		return result;
	}
	
	public int getUsage() {
		return usage;
	}
	
	public void increaseUsage()
	{
		usage++;
	}
}
