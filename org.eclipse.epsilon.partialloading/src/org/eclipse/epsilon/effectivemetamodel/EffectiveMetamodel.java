package org.eclipse.epsilon.effectivemetamodel;

import java.util.ArrayList;

public class EffectiveMetamodel {

	protected String name;
	protected String nsuri;
	protected String path;
	protected boolean isCalculated = false; // To understand that effective metamodel is calculated or not
	protected ArrayList<EffectiveType> allOfType = new ArrayList<EffectiveType>();
	protected ArrayList<EffectiveType> allOfKind = new ArrayList<EffectiveType>();
	protected ArrayList<EffectiveType> types = new ArrayList<EffectiveType>();

	public EffectiveMetamodel(String name, String nsuri) {
		this.name = name;
		this.nsuri = nsuri;
	}

	public EffectiveMetamodel() {
	}

	public void setIsCalculated(boolean set) {
		this.isCalculated = set;
	}

	public boolean getIsCalculated() {
		return isCalculated;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNsuri(String nsuri) {
		this.nsuri = nsuri;
	}

	public void clear() {
		this.getAllOfKind().clear();
		this.getAllOfType().clear();
		this.getTypes().clear();
		this.isCalculated = false;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getNsuri() {
		return nsuri;
	}

	public ArrayList<EffectiveType> getAllOfType() {
		return allOfType;
	}

	public ArrayList<EffectiveType> getAllOfKind() {
		return allOfKind;
	}

	public void addAttributeToAll(String modelElement) {
		for (EffectiveType et : allOfKind)
			addAttributeToEffectiveType(et, modelElement);
		for (EffectiveType et : allOfType)
			addAttributeToEffectiveType(et, modelElement);
		for (EffectiveType et : types)
			addAttributeToEffectiveType(et, modelElement);
	}

	public EffectiveType addToAllOfKind(String modelElement) {
		for (EffectiveType et : allOfKind) {
			if (et.getName().equals(modelElement)) {
				return et;
			}
		}
		if (allOfTypeContains(modelElement)) {
			EffectiveType et = addToAllOfKind(getFromAllOfType(modelElement));
			removeFromTypes(modelElement);
			return et;
		} else if (typesContains(modelElement)) {
			EffectiveType et = addToAllOfKind(getFromTypes(modelElement));
			removeFromTypes(modelElement);
			return et;
		}

		EffectiveType et = new EffectiveType(modelElement);
		et.setEffectiveMetamodel(this);
		allOfKind.add(et);
		return et;
	}

	public EffectiveType addToAllOfKind(EffectiveType et) {
		for (EffectiveType t : allOfKind) {
			if (t.getName().equals(et.getName())) {
				return t;
			}
		}
		et.setEffectiveMetamodel(this);
		allOfKind.add(et);
		return et;
	}

	public EffectiveType addToAllOfType(String modelElement)

	{
		for (EffectiveType et : allOfType) {
			if (et.getName().equals(modelElement)) {
				et.increaseUsage();
				return et;
			}
		}

		EffectiveType et = new EffectiveType(modelElement);
		et.setEffectiveMetamodel(this);
		allOfType.add(et);
		return et;
	}

	public EffectiveType addToAllOfType(EffectiveType efType)

	{
		for (EffectiveType et : allOfType) {
			if (et.getName().equals(efType.getName())) {
				et.increaseUsage();
				return et;
			}
		}

		efType.setEffectiveMetamodel(this);
		allOfType.add(efType);
		return efType;
	}

	public EffectiveType addToTypes(String modelElement)

	{
		for (EffectiveType et : types) {
			if (et.getName().equals(modelElement)) {
				return et;
			}
		}
		EffectiveType et = new EffectiveType(modelElement);
		et.setEffectiveMetamodel(this);
		types.add(et);
		return et;
	}

	public EffectiveType addToTypes(EffectiveType efType)

	{
		for (EffectiveType et : types) {
			if (et.getName().equals(efType.getName())) {
				return et;
			}
		}
		efType.setEffectiveMetamodel(this);
		types.add(efType);
		return efType;
	}

	/* Get methods for all references of effective meta-model */
	public ArrayList<EffectiveType> getTypes() {
		return types;
	}

	/* Methods for adding new feature to effective types (where-ever they are!) */
	public EffectiveFeature addAttributeToEffectiveType(EffectiveType effectiveType, String attribute) {

		if (effectiveType != null) {
			EffectiveFeature effectiveFeature = new EffectiveFeature(attribute);
			for (EffectiveFeature ef : effectiveType.getAttributes())
				if (ef.getName().equals(attribute))
					return effectiveFeature;
			effectiveType.getAttributes().add(effectiveFeature);
			return effectiveFeature;
		}
		return null;
	}

	public EffectiveFeature addReferenceToEffectiveType(String elementName, String reference) {
		EffectiveType effectiveType = getFromAllOfKind(elementName);
		if (effectiveType == null) {
			effectiveType = getFromAllOfType(elementName);
			if (effectiveType == null)
				effectiveType = getFromTypes(elementName);
		}
		if (effectiveType != null) {
			EffectiveFeature effectiveFeature = new EffectiveFeature(reference);
			for (EffectiveFeature ef : effectiveType.getReferences())
				if (ef.getName().equals(reference))
					return effectiveFeature;
			effectiveType.getReferences().add(effectiveFeature);
			return effectiveFeature;
		}
		return null;
	}

	public boolean removeFromTypes(String elementName) {
		EffectiveType effectiveType = getFromTypes(elementName);
		if (effectiveType != null) {
			return getTypes().remove(effectiveType);
		}
		return false;
	}

	public boolean removeFromAllOfType(String elementName) {
		EffectiveType effectiveType = getFromAllOfType(elementName);
		if (effectiveType != null) {
			return getTypes().remove(effectiveType);
		}
		return false;
	}

	public ArrayList<EffectiveType> getAllOfEffectiveTypes() {
		ArrayList<EffectiveType> classes = new ArrayList<EffectiveType>();
		classes.addAll(getAllOfKind());
		classes.addAll(getAllOfType());
		classes.addAll(getTypes());
		return classes;
	}

	public boolean hasEffectiveType(String typeName) {
		for (EffectiveType efType : getAllOfEffectiveTypes()) {
			if (efType.getName().equals(typeName))
				return true;
		}
		return false;
	}

	/* Get elements from allOfType */
	public EffectiveType getFromAllOfType(String elementName) {
		for (EffectiveType ef : allOfType) {
			if (ef.getName().equals(elementName)) {
				return ef;
			}
		}
		return null;
	}

	/* Get elements from types */
	public EffectiveType getFromTypes(String elementName) {
		for (EffectiveType ef : types) {
			if (ef.getName().equals(elementName)) {
				return ef;
			}
		}
		return null;
	}

	/* Get elements from allOfKind */
	public EffectiveType getFromAllOfKind(String elementName) {
		for (EffectiveType ef : allOfKind) {
			if (ef.getName().equals(elementName)) {
				return ef;
			}
		}
		return null;
	}

	/* Check if an element is exists in allOfType */
	public boolean allOfTypeContains(String modelElement) {
		for (EffectiveType ef : allOfType) {
			if (ef.getName().equals(modelElement)) {
				return true;
			}
		}
		return false;
	}

	/* Check if an element is exists in allOfKind */
	public boolean allOfKindContains(String modelElement) {
		for (EffectiveType ef : allOfKind) {
			if (ef.getName().equals(modelElement)) {
				return true;
			}
		}
		return false;
	}

	/* Check if an element is exists in types */
	public boolean typesContains(String modelElement) {
		for (EffectiveType ef : types) {
			if (ef.getName().equals(modelElement)) {
				return true;
			}
		}
		return false;
	}

	public void copyEffectiveMetamodel(EffectiveMetamodel model) {
		for (EffectiveType ef : this.getAllOfKind())
			model.addToAllOfKind(ef);
		for (EffectiveType ef : this.getAllOfType())
			model.addToAllOfType(ef);
		for (EffectiveType ef : this.getTypes())
			model.addToTypes(ef);
	}

	public boolean haveOverlap(EffectiveMetamodel model) {
		for (EffectiveType efType : getAllOfEffectiveTypes())
			if (model.hasEffectiveType(efType.getName()))
				return true;
		return false;
	}
	
	public void print() {
		System.out.println("*****allOfKind*****" );
		for (EffectiveType ef : this.getAllOfKind()) {
			System.out.print(ef.getName());
			for(EffectiveFeature f : ef.getAttributes())
			System.out.print(", attr = " + f.getName());
			for(EffectiveFeature f : ef.getReferences())
				System.out.print(", refs = " + f.getName());
			System.out.println();
		}
		System.out.println("*****Types*****" );
		for (EffectiveType ef : this.getTypes()) {
			System.out.println(ef.getName());
			for(EffectiveFeature f : ef.getAttributes())
				System.out.println("attr = " + f.getName());
			for(EffectiveFeature f : ef.getAttributes())
				System.out.println("refs = " + f.getName());
		}
	}
}
