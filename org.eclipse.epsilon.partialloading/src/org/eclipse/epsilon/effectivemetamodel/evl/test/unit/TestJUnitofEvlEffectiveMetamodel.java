package org.eclipse.epsilon.effectivemetamodel.evl.test.unit;

import static org.junit.Assert.assertEquals;


import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.junit.Test;

public class TestJUnitofEvlEffectiveMetamodel{

	String metamodel = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/flowchart.ecore";
	
	@Test
	public void MethodContextTest() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/methodContextAny.evl";
		XMIN actualEf = null, expectedEf;
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.addToAllOfKind("Action");
		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		
		/*actual*/
		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}

	@Test
	public void CollectionOfAnyTest() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/collectionAnyType.evl";
		XMIN actualEf = null, expectedEf;
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.addToAllOfKind("Decision");
		expectedEf.getFromAllOfKind("Decision").addToAttributes("name");
		expectedEf.addToAllOfKind("Action");
		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		
		/*Actual*/
		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}
	
	@Test
	public void CheckDiffrentAssignmentTest() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/checkAssignment.evl";
		XMIN actualEf = null, expectedEf;
		
		/*Expected*/
		expectedEf = new XMIN();
		expectedEf.addToAllOfKind("Decision");
		expectedEf.getFromAllOfKind("Decision").addToAttributes("name");
		expectedEf.getFromAllOfKind("Decision").addToReferences("outgoing");
		expectedEf.addToAllOfKind("Action");
		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.getFromAllOfKind("Action").addToReferences("outgoing");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Transition").addToReferences("source");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		expectedEf.addToAllOfKind("Node");
		expectedEf.getFromAllOfKind("Node").addToAttributes("name");
		expectedEf.getFromAllOfKind("Node").addToReferences("outgoing");
		
		/*Actual*/
		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
		assertEquals("Failed", effectiveMetamodelConvertor(expectedEf).equals(effectiveMetamodelConvertor(actualEf)), true);
	}
public String effectiveMetamodelConvertor(XMIN model) {
	String XMIN = null;
	for (EffectiveType type : model.getAllOfKind()) {
		XMIN += "AllofKind" + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			XMIN += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			XMIN += et.getName() + "-";
	}
	for (EffectiveType type : model.getAllOfType()) {
		XMIN += "AllofKind" + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			XMIN += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			XMIN += et.getName() + "-";
	}
	for (EffectiveType type : model.getTypes()) {
		XMIN += "AllofKind" + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			XMIN += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			XMIN += et.getName() + "-";
	}
	return XMIN;
	}
}