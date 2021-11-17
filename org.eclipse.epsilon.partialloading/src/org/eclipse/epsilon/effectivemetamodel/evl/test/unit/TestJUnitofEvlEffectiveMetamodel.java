package org.eclipse.epsilon.effectivemetamodel.evl.test.unit;

import static org.junit.Assert.assertEquals;

import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.junit.Test;

public class TestJUnitofEvlEffectiveMetamodel{

	String metamodel = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/flowchart.ecore";
	
	@Test
	public void PartiotioningTest() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/flowchartConstraints.evl";
		EffectiveMetamodel actualEf = null, expectedEf;
		
		
		/*Expected*/
		expectedEf = new EffectiveMetamodel();
		expectedEf.addToAllOfKind("Action");
		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		
		/*actual*/
		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
		//actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
	}
	
//	@Test
//	public void MethodContextTest() throws Exception {
//		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/methodContextAny.evl";
//		EffectiveMetamodel actualEf = null, expectedEf;
//		
//		/*Expected*/
//		expectedEf = new EffectiveMetamodel();
//		expectedEf.addToAllOfKind("Action");
//		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
//		expectedEf.addToAllOfKind("Transition");
//		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
//		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
//		
//		/*actual*/
//		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
//		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
//	}
//
//	@Test
//	public void CollectionOfAnyTest() throws Exception {
//		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/collectionAnyType.evl";
//		EffectiveMetamodel actualEf = null, expectedEf;
//		
//		/*Expected*/
//		expectedEf = new EffectiveMetamodel();
//		expectedEf.addToAllOfKind("Decision");
//		expectedEf.getFromAllOfKind("Decision").addToAttributes("name");
//		expectedEf.addToAllOfKind("Action");
//		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
//		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
//		expectedEf.addToAllOfKind("Transition");
//		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
//		
//		/*Actual*/
//		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
//		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
//	}
//	
//	@Test
//	public void CheckDiffrentAssignmentTest() throws Exception {
//		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/checkAssignment.evl";
//		EffectiveMetamodel actualEf = null, expectedEf;
//		
//		/*Expected*/
//		expectedEf = new EffectiveMetamodel();
//		expectedEf.addToAllOfKind("Decision");
//		expectedEf.getFromAllOfKind("Decision").addToAttributes("name");
//		expectedEf.getFromAllOfKind("Decision").addToReferences("outgoing");
//		expectedEf.addToAllOfKind("Action");
//		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
//		expectedEf.getFromAllOfKind("Action").addToReferences("outgoing");
//		expectedEf.addToAllOfKind("Transition");
//		expectedEf.getFromAllOfKind("Transition").addToReferences("source");
//		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
//		expectedEf.addToAllOfKind("Node");
//		expectedEf.getFromAllOfKind("Node").addToAttributes("name");
//		expectedEf.getFromAllOfKind("Node").addToReferences("outgoing");
//		
//		/*Actual*/
//		actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
//		assertEquals("Failed", effectiveMetamodelConvertor(expectedEf).equals(effectiveMetamodelConvertor(actualEf)), true);
//	}
public String effectiveMetamodelConvertor(EffectiveMetamodel model) {
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