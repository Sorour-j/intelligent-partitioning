package org.eclipse.epsilon.effectivemetamodel.Junittest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.epsilon.SmartSaxParser.Demonstration;
import org.eclipse.epsilon.common.parse.Region;
import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.XMIN;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtraction;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodelExtractor;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.Statement;
import org.eclipse.epsilon.eol.parse.Eol_EolParserRules.returnStatement_return;
import org.junit.Test;

public class TestJUnitofEvlEffectiveMetamodel{

	String metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart.ecore";
	//String model = "src/org/eclipse/epsilon/TestUnit/Parser/flowchart2.xmi";
	
	@Test
	public void MethodContext() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/Junittest/methodContextAny.evl";
		XMIN actualEf = null, expectedEf;
		
		expectedEf = new XMIN();
		expectedEf.addToAllOfKind("Action");
		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		
		actualEf = main.calculation(evlFile);
		assertEquals("Failed", effectiveMetamodelConvertor(expectedEf).equals(effectiveMetamodelConvertor(actualEf)), true);
	}

	@Test
	public void CollectionAny() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/Junittest/collectionAnyType.evl";
		XMIN actualEf = null, expectedEf;
		
		expectedEf = new XMIN();
		expectedEf.addToAllOfKind("Decision");
		expectedEf.getFromAllOfKind("Decision").addToAttributes("name");
		expectedEf.addToAllOfKind("Action");
		expectedEf.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.getFromAllOfKind("Action").addToReferences("incoming");
		expectedEf.addToAllOfKind("Transition");
		expectedEf.getFromAllOfKind("Transition").addToAttributes("name");
		actualEf = main.calculation(evlFile);
		assertEquals("Failed", effectiveMetamodelConvertor(expectedEf).equals(effectiveMetamodelConvertor(actualEf)), true);
	}
	
	@Test
	public void CheckDiffrentAssignment() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/Junittest/checkAssignment.evl";
		XMIN actualEf = null, expectedEf;
		
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
		actualEf = main.calculation(evlFile);
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
//	@Test
//	public void allInstances() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllInstancesTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		expectedEf.addToAllOfKind("Node");
//		//expectedEf.addAttributeToAllOfKind("Node", "name");
//		
//		//change the  message*////
//		//assertEquals("Failed", compare (actualEf , expectedEf), true);
//		
//		Demonstration demo = new Demonstration(model); 
//	//	demo.setEfMetamodel(actualEf);
//		actualList = demo.demo();
//		
//		boolean ok =true;
//		int act=0,dec=0;
//		if (actualList.size() == 4)
//		{
//			for (EObject obj : actualList) {
//			
//				
//				if (obj.eClass().getName().equals("Action"))
//					act++;
//				else if (obj.eClass().getName().equals("Decision"))
//					dec++;
//				else
//					ok=false;
//			}
//			if (act != 2 || dec!=2)
//				ok=false;
//		}
//		else 
//			ok = false;
//		
//		assertEquals("Failed", ok, true);
//		
//	}
//	
//	@Test
//	public void AllofTypeforSubClass() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllofTypeforSubClassTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//		//actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		expectedEf.addToAllOfType("Action");
//		//expectedEf.addAttributeToAllOfType("Action", "name");
//		
//	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
//			
//		}
//	
//	@Test
//	public void AllofTypeforSuperClass() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/AllofTypeforSuperClassTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		
//	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
//			
//		}
//	
//	@Test
//	public void CollectionofModelElement() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/CollectionofModelElementTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		expectedEf.addToAllOfType("Action");
//		//expectedEf.addAttributeToAllOfType("Action", "name");
//		expectedEf.addToTypes("Transition");
//		//expectedEf.addAttributeToTypes("Transition", "name");
//		
//		//assertEquals("Failed", compare (actualEf , expectedEf), true);
//		}
//	
//	@Test
//	public void NonContainmentRefrence() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/NonContainmentRefrenceTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		expectedEf.addToAllOfKind("Node");
//		expectedEf.addToTypes("Transition");
//		//expectedEf.addReferenceToAllOfKind("Node", "incoming");
//		
//	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
//		}
//	
//	@Test
//	public void subClass() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/SubClassTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		expectedEf.addToAllOfKind("Action");
//		//expectedEf.addAttributeToAllOfKind("Action", "name");
//		
//		//assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
//		}
//	
//	@Test
//	public void superClass() throws Exception {
//		
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/SuperClassTest.eol";
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//		//actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("flowchart");
//		expectedEf.addToAllOfKind("Node");
////		expectedEf.addAttributeToAllOfKind("Node", "name");
////		
////		assertEquals("Failed", compare (actualEf , expectedEf), true);
////	
//		}
//	@Test
//	public void contaimentRefrence() throws Exception {
//		
//		metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/componentLanguage.ecore";
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/ContainmentRefrenceTest.eol";
//		
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//		//actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("componentLanguage");
//		expectedEf.addToAllOfKind("Component");
//	//	expectedEf.addReferenceToAllOfKind("Component","ports");
//		expectedEf.addToTypes("Port");
//		
//		//assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
//		}
//	@Test
//	public void EnumerationType() throws Exception {
//		
//		metamodel = "src/org/eclipse/epsilon/TestUnit/Parser/componentLanguage.ecore";
//		String eolFile = "src/org/eclipse/epsilon/TestUnit/Parser/EnumerationTypeTest.eol";
//		
//		XMIN actualEf,expectedEf;
//		
//		/*Actual*/
//		EffectiveMetamodelExtraction ef = new EffectiveMetamodelExtraction(metamodel, eolFile);
//	//	actualEf = ef.getEffectiveMetamodel();
//		
//		/*Expected*/
//		expectedEf = new XMIN();
//		expectedEf.setName("componentLanguage");
//		expectedEf.addToAllOfKind("Component");
////		expectedEf.addReferenceToAllOfKind("Component","ports");
////		expectedEf.addToTypes("Port");
////		expectedEf.addAttributeToTypes("Port", "type");
////		
//	//	assertEquals("Failed", compare (actualEf , expectedEf), true);
//	
//		}
//	
//	
	

