package org.eclipse.epsilon.effectivemetamodel.evl.test.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.epsilon.effectivemetamodel.EffectiveFeature;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.effectivemetamodel.EffectiveType;
import org.junit.Test;

public class TestJUnitofEvlPartitioningEffectiveMetamodel2{

	String metamodel = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/flowchart.ecore";
	
	@Test
	public void PartiotioningTest() throws Exception {
		String evlFile = "src/org/eclipse/epsilon/effectivemetamodel/evl/test/unit/flowchartConstraints.evl";
		HashMap<String, EffectiveMetamodel> actualEf = new HashMap<String, EffectiveMetamodel>();
		HashMap<String, EffectiveMetamodel> expectedEf= new HashMap<String, EffectiveMetamodel>();
		EffectiveMetamodel efMetamodel;
		
		
		/*Expected*/
		efMetamodel = new EffectiveMetamodel();
		efMetamodel.addToAllOfKind("Action");
		efMetamodel.getFromAllOfKind("Action").addToAttributes("name");
		expectedEf.put("LabelsStartWithT",efMetamodel);
		
		efMetamodel = new EffectiveMetamodel();
		
		efMetamodel.addToAllOfKind("Action");
		efMetamodel.getFromAllOfKind("Action").addToReferences("incoming");
		efMetamodel.addToAllOfKind("Transition");
		efMetamodel.getFromAllOfKind("Transition").addToAttributes("name");
		expectedEf.put("NotNull", efMetamodel);
		
		/*actual*/
		actualEf = SetEvlCalculationPartitioningSetting.calculation(evlFile , metamodel);
		ArrayList<EffectiveMetamodel> arr = new ArrayList<EffectiveMetamodel>(actualEf.values());
		
		//actualEf = SetEvlCalculationSetting.calculation(evlFile , metamodel);
		assertEquals(effectiveMetamodelConvertor(expectedEf), effectiveMetamodelConvertor(actualEf));
		System.out.println(arr.get(0).haveOverlap(arr.get(1)));
	}
	

public String effectiveMetamodelConvertor(HashMap<String, EffectiveMetamodel> model) {
	String efMM = new String();
	Iterator it = model.entrySet().iterator();
	
	while (it.hasNext()) {
		
		HashMap.Entry pair = (HashMap.Entry)it.next();
		
		efMM += pair.getKey() + "-";
		EffectiveMetamodel ef = (EffectiveMetamodel) pair.getValue();
		
		for (EffectiveType type : ef.getAllOfKind()) {
			efMM += "AllofKind" + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			efMM += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			efMM += et.getName() + "-";
	}
	for (EffectiveType type : ef.getAllOfType()) {
		efMM += "AllofKind" + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			efMM += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			efMM += et.getName() + "-";
	}
	for (EffectiveType type : ef.getTypes()) {
		efMM += "AllofKind" + type.getName() + "-";
		for (EffectiveFeature et : type.getAttributes())
			efMM += et.getName()+ "-";
		for (EffectiveFeature et : type.getReferences())
			efMM += et.getName() + "-";
	}
	}
	return efMM;
	}
}