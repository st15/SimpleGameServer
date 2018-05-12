package jam.core.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class UserManager 
{
	private Map<Integer, User> usersById = new ConcurrentHashMap<Integer, User>();
	private Map<String, User> usersByName = new ConcurrentHashMap<String, User>();
	private List<User> users;
	private SimpleJdbcTemplate jdbcTemplate; 
	
	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}
	
	protected void selectUsers()
	{
		users = jdbcTemplate.query("SELECT * FROM user", new MyRowMapper());
	}
	
	public List<User> getUsers()
	{
		if(users == null)
			selectUsers();
		return users;
	}
	
	public User getUser(int id)
	{
		return null;
	}
	
	public User getUser(String userName, String password) throws LoginException
	{
		User user = usersByName.get(userName);
		if (user != null) 
		{
			if (user.getPassword().equals(password)) 
				return user;
			else
				throw new LoginException("Wrong username or password.");
		}
		else
		{
			try
			{
				user = jdbcTemplate.queryForObject(
						"SELECT * FROM user WHERE username=? AND password=? LIMIT 1", 
						new MyRowMapper(), userName, password);
			}
			catch (IncorrectResultSizeDataAccessException e)
			{
				System.out.println("No user in db.");
				throw new LoginException("Wrong username or password.");
			}
			usersByName.put(userName, user);
			usersById.put(user.getId(), user);
			users.add(user);
			return user;
		}
	}
	/**
	 * За всеки ред от ResultSet създава обект User
	 */
	protected class MyRowMapper implements RowMapper<User>
	{
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User(rs.getInt("id"), rs.getString("username"),
					rs.getString("password"), rs.getInt("avatar"),
					rs.getByte("gender"), rs.getInt("date_register"));

			return user;
		}
	}
}
