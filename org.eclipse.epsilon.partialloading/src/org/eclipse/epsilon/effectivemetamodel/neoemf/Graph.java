/**
 */
package org.eclipse.epsilon.effectivemetamodel.neoemf;

import fr.inria.atlanmod.neoemf.core.PersistentEObject;
import org.eclipse.emf.common.util.EList;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Graph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link Graph#getVertices <em>Vertices</em>}</li>
 *   <li>{@link Graph#getEdges <em>Edges</em>}</li>
 * </ul>
 *
 * @see GraphPackage#getGraph()
 * @model
 * @extends PersistentEObject
 * @generated
 */
public interface Graph extends PersistentEObject {
	/**
	 * Returns the value of the '<em><b>Vertices</b></em>' containment reference list.
	 * The list contents are of type {@link Vertice}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vertices</em>' containment reference list.
	 * @see GraphPackage#getGraph_Vertices()
	 * @model containment="true"
	 * @generated
	 */
	EList<Vertice> getVertices();

	/**
	 * Returns the value of the '<em><b>Edges</b></em>' containment reference list.
	 * The list contents are of type {@link Edge}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Edges</em>' containment reference list.
	 * @see GraphPackage#getGraph_Edges()
	 * @model containment="true"
	 * @generated
	 */
	EList<Edge> getEdges();

} // Graph
