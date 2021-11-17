package org.eclipse.epsilon.emc.emfmysql;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.effectivemetamodel.EffectiveMetamodel;
import org.eclipse.epsilon.emc.emf.EmfModelMetamodel;
import org.eclipse.epsilon.emc.jdbc.JdbcModel;
import org.eclipse.epsilon.emc.jdbc.ResultSetList;
import org.eclipse.epsilon.emc.jdbc.Table;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.m3.Metamodel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;

import com.mysql.jdbc.Driver;

public class EmfMySqlModel extends JdbcModel{

	public static String PROPERTY_METAMODELNSURI = "metamodelNsuri";
	protected String metamodelNsuri;
	protected EffectiveMetamodel efMetamodel;
	
	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		super.load(properties, resolver);
		this.metamodelNsuri = properties.getProperty(PROPERTY_METAMODELNSURI);
	}
	
	@Override
	public Metamodel getMetamodel(StringProperties properties, IRelativePathResolver resolver) {
		return new EmfModelMetamodel(properties, resolver);
	}
	@Override
	public ResultSet getResultSet(String selection, String condition, List<Object> parameters, Table table,
			boolean streamed, boolean one) {
		
		
		try {
			
			String sql = "select " + selection + " from " + table.getName();
			if (condition != null && condition.trim().length() > 0) {
				sql += " where " + condition;
			}
			if (one) {
				sql += " limit 1";
			}
			
			
			//String query = "1		// System.err.println(sql);

			int options = ResultSet.TYPE_SCROLL_INSENSITIVE;
			int resultSetType = this.getResultSetType();

			if (streamed) {
				options = ResultSet.TYPE_FORWARD_ONLY;
				resultSetType = ResultSet.CONCUR_READ_ONLY;
			}

			System.out.println(sql);
			
		//	System.out.println(parameters);
			PreparedStatement preparedStatement = this.prepareStatement(sql, options, resultSetType, streamed);
			
			if (streamed) {
				preparedStatement.setFetchSize(Integer.MIN_VALUE);
			} else {
				preparedStatement.setFetchSize(Integer.MAX_VALUE);
			}

			if (parameters != null) {
				this.setParameters(preparedStatement, parameters);
			}

			ResultSet resultSet = preparedStatement.executeQuery();
		//	PreparedStatement preparedStatements = this.prepareStatement(query, options, resultSetType, streamed);
			print(resultSet);
//			ResultSet resultSets = preparedStatements.executeQuery();
//			print(resultSets);
			
			if (streamed) {
				connectionPool.register(resultSet, preparedStatement.getConnection());
			}
			return resultSet;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Collection<?> getAllOfType(String type) throws EolModelElementTypeNotFoundException {
		Collection<?> ret = null;
		try {
			ret = new EffectiveResultSetList(this, database.getTable(type), "", null, streamResults, false);
		} catch (Exception e) {
			// suppress non-wrapped table names exception
		}
		type = type.replace("'", identifierQuoteString);
		ret = new EffectiveResultSetList(this, database.getTable(identifierQuoteString + type + identifierQuoteString), "", null,
				streamResults, false);
		return ret;
	}
	
	@Override
	protected Driver createDriver() throws SQLException {
		return new Driver();
	}

	@Override
	protected String getJdbcUrl() {
		//return "emfmysql:mysql://" + server + ":" + port + "/" + databaseName;
		return "jdbc:mysql://" + server + ":" + port + "/" + databaseName;
	}
	public String getMetamodelNsuri() {
		return metamodelNsuri;
	}

	public void setMetamodelNsuri(String metamodelNsuri) {
		this.metamodelNsuri = metamodelNsuri;
	}
	public void setEffectiveMetamodel(EffectiveMetamodel efMetamodel) {
		this.efMetamodel = efMetamodel;
	}
	public EffectiveMetamodel getEffectiveMetamodel() {
		 return efMetamodel;
	}
}
