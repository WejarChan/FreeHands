package org.micheal.freeHands.template;

import org.micheal.freeHands.template.PaginationInfo.SortOrder;

public class Dialect {
	
	private DatabaseType databaseType;

	public Dialect(){
		
	}
	
	public Dialect(DatabaseType type){
		this.databaseType = type;
	}
	
	public String getPaginationSql(String sql,PaginationInfo pageInfo) throws Exception{
		
		if(databaseType.equals(DatabaseType.oracle)){
			return getOraclePaginationSql(sql,pageInfo);
		}else if(databaseType.equals(DatabaseType.mysql)){
			return getMysqlPaginationSql(sql,pageInfo);
		}else{
			throw new Exception("databaseType ["+databaseType+"] in Dialect is not defined!");
		}
	}
	
	public String getTotalRowNumSql(String sql){
		sql = sql.toUpperCase();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM (");
		sb.append(sql);
		sb.append(") C");
		return sb.toString();
	}
	
	private String getMysqlPaginationSql(String sql, PaginationInfo pageInfo) {
		sql = sql.toUpperCase();
		pageInfo.init();
		String sortCol = pageInfo.getSortCol();
		SortOrder sortOrder = pageInfo.getSortOrder();
		StringBuffer sb = new StringBuffer();
		sb.append(sql);
		if(sortCol != null && sortOrder != null){
			sb.append(" ORDER BY "+sortCol.toUpperCase()+" "+(sortOrder.equals(SortOrder.asc) ? "ASC" : "DESC"));
		}
		sb.append(" LIMIT "+pageInfo.getOffSet()+", "+pageInfo.getLimit());
		return sb.toString();
	}

	private String getOraclePaginationSql(String sql, PaginationInfo pageInfo) {
		sql = sql.toUpperCase();
		pageInfo.init();
		String sortCol = pageInfo.getSortCol();
		SortOrder sortOrder = pageInfo.getSortOrder();
		
		StringBuffer sb = new StringBuffer();
		int firstFromIndex = sql.indexOf("FROM")-1;
		//select * 
		sb.append(sql.substring(0,firstFromIndex));
		//select * from (
		sb.append(" FROM (");
		//select * from (select *
		sb.append(sql.substring(0,firstFromIndex));
		//select * from (select *,rownum as rn from {
		sb.append(",ROWNUM AS RN FROM (");
		//select * from (select *,rownum as rn from ( 原sql
		sb.append(sql);
		if(sortCol != null && sortOrder != null){
			sb.append(" ORDER BY "+sortCol.toUpperCase()+" "+(sortOrder.equals(SortOrder.asc) ? "ASC" : "DESC"));
		}
		//select * from (select *,rownum as rn from ( 原sql ) rownum <= 40
		sb.append(") ROWNUM <= "+(pageInfo.getOffSet()+pageInfo.getLimit()+1));
		//select * from (select *,rownum as rn from ( 原sql ) rownum <= 40 ) where rn >= 31
		sb.append(" ) WHERE RN >= "+pageInfo.getOffSet()+1);
		
		return sb.toString();
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}
	
	public enum DatabaseType{
		oracle,mysql;
	}
}
