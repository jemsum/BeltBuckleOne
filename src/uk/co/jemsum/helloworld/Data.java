package uk.co.jemsum.helloworld;

import java.util.List;

public class Data
{
	String as_of;
	String created_at;
	List<Location> locations;
	List<Trend> trends;
	
	
	public String getAs_of()
	{
		return as_of;
	}
	public void setAs_of(String as_of)
	{
		this.as_of = as_of;
	}
	public String getCreated_at()
	{
		return created_at;
	}
	public void setCreated_at(String created_at)
	{
		this.created_at = created_at;
	}
	public List<Location> getLocations()
	{
		return locations;
	}
	public void setLocations(List<Location> locations)
	{
		this.locations = locations;
	}
	public List<Trend> getTrends()
	{
		return trends;
	}
	public void setTrends(List<Trend> trends)
	{
		this.trends = trends;
	}
	
}
