package org.eclipse.epsilon.effectivemetamodel;

import java.util.ArrayList;

import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.compile.m3.Attribute;
import org.eclipse.epsilon.eol.compile.m3.MetaClass;
import org.eclipse.epsilon.eol.compile.m3.Reference;
import org.eclipse.epsilon.eol.compile.m3.StructuralFeature;
import org.eclipse.epsilon.eol.dom.ExpressionStatement;
import org.eclipse.epsilon.eol.dom.NameExpression;
import org.eclipse.epsilon.eol.dom.OperationCallExpression;
import org.eclipse.epsilon.eol.dom.PropertyCallExpression;
import org.eclipse.epsilon.eol.dom.StringLiteral;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolModelElementType;
import org.eclipse.epsilon.erl.dom.NamedStatementBlockRule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.dom.ConstraintContext;

public class EvlEffectiveMetamodelExtractor {

	public EvlEffectiveMetamodelExtractor() {}
	
	public XMIN geteffectiveMetamodel(IEolModule eolmodule) {
		
		XMIN smartEMFModel = null;
		ArrayList<ModuleElement> children = new ArrayList<ModuleElement>();
		ArrayList<StructuralFeature> features = new ArrayList<StructuralFeature>();
		EffectiveType effectiveType;
		EolModelElementType target;
		EvlModule module = (EvlModule)eolmodule;
		
		try {
			smartEMFModel = (XMIN) module.getContext().getModelRepository().getModelByName
					(module.getCompilationContext().getModelDeclarations().get(0).getNameExpression()
							.getName());
		} catch (EolModelNotFoundException e) {
			e.printStackTrace();
		}
		children.addAll(module.getChildren());
		
	//	int i = 0;
		while (!(children.isEmpty())) {

			ModuleElement MD = children.get(0);
			children.remove(MD);
			
			
			if ((!(MD.getChildren().isEmpty())))
				children.addAll(MD.getChildren());
			
			if (MD instanceof ConstraintContext && ((ConstraintContext)MD).getTypeExpression()!=null) {
							
			
				target = (EolModelElementType)((ConstraintContext)MD).getTypeExpression().getResolvedType();
			//	smartEMFModel.addToAllOfKind(target.getName());
				
				if (smartEMFModel.allOfTypeContains(target.getTypeName())) {
					
					smartEMFModel.addToAllOfKind(smartEMFModel.getFromAllOfType(target.getName()));
					smartEMFModel.removeFromAllOfType(target.getName());
					
//					ExpressionStatement statement = new ExpressionStatement();
//					statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"), new OperationCallExpression(new NameExpression(smartEMFModel.getName()),new NameExpression("getFromAllOfType"), new StringLiteral(target.getName()))));
//					//module.getMain().getStatements().add(i, statement);
//					((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//	
//					i++;
//					
//					ExpressionStatement statement2 = new ExpressionStatement();
//					statement2.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("removeFromAllOfType"), new StringLiteral(target.getName())));
//					//module.getMain().getStatements().add(i, statement2);
//					((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement2);
//					
//					i++;
				}
				//If the element is already under EM's types reference
				if (smartEMFModel.typesContains(target.getTypeName())){
				
					smartEMFModel.addToAllOfKind(smartEMFModel.getFromTypes(target.getName()));
					smartEMFModel.removeFromTypes(target.getName());
					
//					ExpressionStatement statement = new ExpressionStatement();
//					statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"), new OperationCallExpression(new NameExpression(smartEMFModel.getName()),new NameExpression("getFromTypes"), new StringLiteral(target.getName()))));
//					//module.getMain().getStatements().add(i, statement);
//					((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//					
//					i++;
//					
//					ExpressionStatement statement2 = new ExpressionStatement();
//					statement2.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("removeFromTypes"), new StringLiteral(target.getName())));
//					//module.getMain().getStatements().add(i, statement2);
//					((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement2);
//					
//					i++;							
				}
				else {
					// not already under the EM's allOfKind or allOfType references
						smartEMFModel.addToAllOfKind(target.getTypeName());
//						ExpressionStatement statement = new ExpressionStatement();
//						statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"),new StringLiteral(target.getTypeName())));
//						//module.getMain().getStatements().add(i, statement);
//						((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//						
//						i++;
				}
			}
			
			if (MD instanceof OperationCallExpression) {

				OperationCallExpression operationCall = (OperationCallExpression) MD;
				
				if (!operationCall.isContextless()) {

					if (operationCall.getTargetExpression().getResolvedType() instanceof EolModelElementType) {

						target = (EolModelElementType) operationCall.getTargetExpression().getResolvedType();
					
						if (operationCall.getNameExpression().getName().equals("all")
								|| operationCall.getNameExpression().getName().equals("allOfKind")
								|| operationCall.getNameExpression().getName().equals("allInstances")) {

							//If the element is already under EM's allOfType reference
							if (smartEMFModel.allOfTypeContains(target.getTypeName())) {
								
								smartEMFModel.addToAllOfKind(smartEMFModel.getFromAllOfType(target.getName()));
								smartEMFModel.removeFromAllOfType(target.getName());
								
//								ExpressionStatement statement = new ExpressionStatement();
//								statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"), new OperationCallExpression(new NameExpression(smartEMFModel.getName()),new NameExpression("getFromAllOfType"), new StringLiteral(target.getName()))));
//								//module.getMain().getStatements().add(i, statement);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//								
//								i++;
//								
//								ExpressionStatement statement2 = new ExpressionStatement();
//								statement2.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("removeFromAllOfType"), new StringLiteral(target.getName())));
//								//module.getMain().getStatements().add(i, statement2);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement2);
//								
//								i++;
							}
							//If the element is already under EM's types reference
							if (smartEMFModel.typesContains(target.getTypeName())){
							
								smartEMFModel.addToAllOfKind(smartEMFModel.getFromTypes(target.getName()));
								smartEMFModel.removeFromTypes(target.getName());
								
//								ExpressionStatement statement = new ExpressionStatement();
//								statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"), new OperationCallExpression(new NameExpression(smartEMFModel.getName()),new NameExpression("getFromTypes"), new StringLiteral(target.getName()))));
//								//module.getMain().getStatements().add(i, statement);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//								
//								i++;
//								
//								ExpressionStatement statement2 = new ExpressionStatement();
//								statement2.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("removeFromTypes"), new StringLiteral(target.getName())));
//								//module.getMain().getStatements().add(i, statement2);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement2);
//								
//								i++;							
							}
							else {
								// not already under the EM's allOfKind or allOfType references
									smartEMFModel.addToAllOfKind(target.getTypeName());
//									ExpressionStatement statement = new ExpressionStatement();
//									statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"),new StringLiteral(target.getTypeName())));
//								//	module.getMain().getStatements().add(i, statement);
//									((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//									
//									i++;
							}

						} else if (operationCall.getNameExpression().getName().equals("allOfType")) {
							if (!smartEMFModel.allOfKindContains(target.getTypeName())
									&& !smartEMFModel.allOfTypeContains(target.getTypeName()))
							{
								smartEMFModel.addToAllOfType(target.getTypeName());
		
//								ExpressionStatement statement = new ExpressionStatement();
//								statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfType"), new StringLiteral(target.getTypeName())));
//							//	module.getMain().getStatements().add(i, statement);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//								
//								i++;
							}
						}
					}
				}
			}
			if (MD instanceof PropertyCallExpression) {

				PropertyCallExpression pro = (PropertyCallExpression) MD;
				if (pro.getTargetExpression().getResolvedType() instanceof EolModelElementType) {

					target = (EolModelElementType) pro.getTargetExpression().getResolvedType();
					
					if (pro.getNameExpression().getName().equals("all") ||pro.getNameExpression().getName().equals("allInstances")) {
						// Like AllofKind Algorithm
						
						//If the element is already under EM's allOfType reference
						if (smartEMFModel.allOfTypeContains(target.getTypeName())) {
							
							smartEMFModel.addToAllOfKind(smartEMFModel.getFromAllOfType(target.getName()));
							smartEMFModel.removeFromAllOfType(target.getName());
							
//							ExpressionStatement statement = new ExpressionStatement();
//							statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"), new OperationCallExpression(new NameExpression(smartEMFModel.getName()),new NameExpression("getFromAllOfType"), new StringLiteral(target.getName()))));
//							//module.getMain().getStatements().add(i, statement);
//							((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//							
//							i++;
//							
//							ExpressionStatement statement2 = new ExpressionStatement();
//							statement2.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("removeFromAllOfType"), new StringLiteral(target.getName())));
//							//module.getMain().getStatements().add(i, statement2);
//							((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement2);
//							
//							i++;
						}
						//If the element is already under EM's types reference
						if (smartEMFModel.typesContains(target.getTypeName())){
						
							smartEMFModel.addToAllOfKind(smartEMFModel.getFromTypes(target.getName()));
							smartEMFModel.removeFromTypes(target.getName());
							
//							ExpressionStatement statement = new ExpressionStatement();
//							statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"), new OperationCallExpression(new NameExpression(smartEMFModel.getName()),new NameExpression("getFromTypes"), new StringLiteral(target.getName()))));
//							//module.getMain().getStatements().add(i, statement);
//							((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//							
//							i++;
//							
//							ExpressionStatement statement2 = new ExpressionStatement();
//							statement2.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("removeFromTypes"), new StringLiteral(target.getName())));
//							//module.getMain().getStatements().add(i, statement2);
//							((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement2);
//							
//							i++;							
						}
						else {
							
							// not already under the EM's allOfKind or allOfType references
								smartEMFModel.addToAllOfKind(target.getTypeName());
//								ExpressionStatement statement = new ExpressionStatement();
//								statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToAllOfKind"),new StringLiteral(target.getTypeName())));
//								//module.getMain().getStatements().add(i, statement);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//								
//								i++;
						}
					}
					// not already under the EM's types, allOfKind or allOfType references
					else {
						effectiveType = new EffectiveType(target.getTypeName());
						effectiveType.setEffectiveMetamodel(smartEMFModel);
						
						if (smartEMFModel.allOfKindContains(effectiveType.getName()))
							effectiveType = smartEMFModel.getFromAllOfKind(effectiveType.getName());
						else if (smartEMFModel.allOfTypeContains(effectiveType.getName()))
							effectiveType = smartEMFModel.getFromAllOfType(effectiveType.getName());
						else if (smartEMFModel.typesContains(effectiveType.getName()))
							effectiveType = smartEMFModel.getFromTypes(effectiveType.getName());
						else
						{
							// add target.getTypeName() under EM's types reference;
							effectiveType = smartEMFModel.addToTypes(effectiveType.getName());
							
//							ExpressionStatement statement = new ExpressionStatement();
//							statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToTypes"), new StringLiteral(effectiveType.getName())));
//							//module.getMain().getStatements().add(i, statement);
//							((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//							
//							i++;
						}
						if (!target.getMetaClass().getAllStructuralFeatures().isEmpty()) {
							features.addAll(target.getMetaClass().getAllStructuralFeatures());
						}

						for (MetaClass metaclass : target.getMetaClass().getSuperTypes()) {
							features.addAll(metaclass.getAllStructuralFeatures());
						}

						for (StructuralFeature sf : features) {
							if (sf instanceof Attribute 
									&& sf.getName().equals(pro.getNameExpression().getName())
									&& !effectiveType.containsAttribute(sf.getName())) {
								effectiveType.addToAttributes(sf.getName());
								
//								ExpressionStatement statement = new ExpressionStatement();
//								statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addAttributeToEffectiveType"), new StringLiteral(effectiveType.getName()) ,new StringLiteral(sf.getName())));
//								//module.getMain().getStatements().add(i, statement);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//								
//								i++;
//								
								break;
							} else if (sf instanceof Reference
									&& sf.getName().equals(pro.getNameExpression().getName())
									&& !effectiveType.containsReference(sf.getName())) {
								effectiveType.addToReferences(sf.getName());
								
//								ExpressionStatement statement = new ExpressionStatement();
//								statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addReferenceToEffectiveType"), new StringLiteral(effectiveType.getName()) ,new StringLiteral(sf.getName())));
//							//	module.getMain().getStatements().add(i, statement);
//								((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//								
//								i++;
								break;
							}
						}
					}
				} else if (pro.getTargetExpression().getResolvedType() instanceof EolCollectionType) {

					if (((EolCollectionType) pro.getTargetExpression().getResolvedType())
							.getContentType() instanceof EolModelElementType) {

						target = (EolModelElementType) ((EolCollectionType) pro.getTargetExpression().getResolvedType())
								.getContentType();
						
						effectiveType = new EffectiveType(target.getTypeName());

						if (smartEMFModel.allOfKindContains(effectiveType.getName()))
							effectiveType = smartEMFModel.getFromAllOfKind(effectiveType.getName());
						else if (smartEMFModel.allOfTypeContains(effectiveType.getName()))
							effectiveType = smartEMFModel.getFromAllOfType(effectiveType.getName());
						
						else {
							// add elementName under EM's types reference;
							effectiveType = smartEMFModel.addToTypes(effectiveType.getName());
						
//							ExpressionStatement statement = new ExpressionStatement();
//							statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addToTypes"), new StringLiteral(effectiveType.getName())));
//							//module.getMain().getStatements().add(i, statement);
//							((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//							
//							i++;
						}
						if (!target.getMetaClass().getAllStructuralFeatures().isEmpty()) {
							features.addAll(target.getMetaClass().getAllStructuralFeatures());
						}
						for (MetaClass metaclass : target.getMetaClass().getSuperTypes()) {
							features.addAll(metaclass.getAllStructuralFeatures());

						}

						for (StructuralFeature sf : features) {
							if (sf instanceof Attribute
									&& sf.getName().equals(pro.getNameExpression().getName())
									&& !effectiveType.containsAttribute(sf.getName())) {
								
									effectiveType.addToAttributes(sf.getName());
//									ExpressionStatement statement = new ExpressionStatement();
//									statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addAttributeToEffectiveType"),new StringLiteral(effectiveType.getName()), new StringLiteral(sf.getName())));
//									//module.getMain().getStatements().add(i, statement);
//									((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//									
//									i++;
									break;
								
							} else if (sf instanceof Reference 
									&& sf.getName().equals(pro.getNameExpression().getName())
									&& !effectiveType.containsReference(sf.getName())) {
									effectiveType.addToReferences(sf.getName());
								
//									ExpressionStatement statement = new ExpressionStatement();
//									statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("addReferenceToEffectiveType"), new StringLiteral(effectiveType.getName()) ,new StringLiteral(sf.getName())));
//								//	module.getMain().getStatements().add(i, statement);
//									((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//									
//									i++;
									break;
							}
						}
					}
				}
			}
		}
		
//		ExpressionStatement statement = new ExpressionStatement();
//		statement.setExpression(new OperationCallExpression(new NameExpression(smartEMFModel.getName()), new NameExpression("load")));
//	//	module.getMain().getStatements().add(i,statement);
//		((NamedStatementBlockRule)module.getDeclaredPre().get(0)).getBody().getStatements().add(i, statement);
//		
//		i++;
//		smartEMFModel.load();
		return smartEMFModel;
	}
}
