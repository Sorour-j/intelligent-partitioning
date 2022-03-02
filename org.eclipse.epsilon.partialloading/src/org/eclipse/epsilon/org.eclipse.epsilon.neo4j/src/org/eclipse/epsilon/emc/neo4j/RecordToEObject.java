package org.eclipse.epsilon.emc.neo4j;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

public class RecordToEObject {

	EObject object;
	
	public RecordToEObject(EffectiveRecord record) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(record.getTypeName());
		for (String s : record.keys()) {
			EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
			attr.setName(s);
			attr.setDefaultValue(record.values().get(0));
			eClass.getEAllAttributes().add(attr);
		}
	}
}
