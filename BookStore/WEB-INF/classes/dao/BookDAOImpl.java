package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import model.Author;
import model.Book;
import model.Category;

public class BookDAOImpl implements BookDAO {

	private String dbPath;

    public BookDAOImpl(ServletContext context) {
        // Use ServletContext to get the real path of the database file
        String relativePath = "/WEB-INF/dbFile/Books.db"; // Adjust the path as necessary
        this.dbPath = context.getRealPath(relativePath);
        if (dbPath == null) {
            throw new IllegalStateException("Database file not found at relative path: " + relativePath);
        }
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String dbURL = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(dbURL);
    }
	private void closeConnection(Connection connection) {
		if (connection == null)
			return;
		try {
			connection.close();
		} catch (SQLException ex) {
		}
	}

	//complete this method
	@Override
	public List<Book> findAllBooks() {
		List<Book> result = new ArrayList<Book>();
		
        // join 3 tables to get needed info
		String sql = "SELECT book.id AS book_id, book.book_title, book.category_id, " +
                "author.first_name, author.last_name, category.category_description " +
                "FROM book " +
                "JOIN author ON book.id = author.book_id " +
                "JOIN category ON book.category_id = category.id";
				
		Connection connection = null;
		try {
			connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
	            Book book = new Book();
	            Author author = new Author();

	            // Populate Book object
	            book.setId(resultSet.getLong("book_id"));
	            book.setBookTitle(resultSet.getString("book_title"));
	            book.setCategoryId(resultSet.getLong("category_id"));
	            book.setCategory(resultSet.getString("category_description"));

	            // Populate Author object
	            author.setFirstName(resultSet.getString("first_name"));
	            author.setLastName(resultSet.getString("last_name"));

	            // Set author to book
	            book.setAuthor(author);

	            result.add(book);
	        }
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return result;
	}

	// complete this method
	@Override
	public List<Book> searchBooksByKeyword(String keyWord) {
		List<Book> result = new ArrayList<Book>();
		
		String sql =  "SELECT book.id AS book_id, book.book_title, book.category_id, " +
                "author.first_name, author.last_name, category.category_description " +
                "FROM book " +
                "JOIN author ON book.id = author.book_id " +
                "JOIN category ON book.category_id = category.id " +
                "WHERE book.book_title LIKE ? " +
                "OR author.first_name LIKE ? " +
                "OR author.last_name LIKE ?";

		Connection connection = null;
		try {

			connection = getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql);
	        String keywordPattern = "%" + keyWord.trim() + "%";
	        statement.setString(1, keywordPattern);
	        statement.setString(2, keywordPattern);
	        statement.setString(3, keywordPattern);
	        ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Book book = new Book();
	            Author author = new Author();

	            // Populate Book object
	            book.setId(resultSet.getLong("book_id"));
	            book.setBookTitle(resultSet.getString("book_title"));
	            book.setCategoryId(resultSet.getLong("category_id"));
	            book.setCategory(resultSet.getString("category_description"));

	            // Populate Author object
	            author.setFirstName(resultSet.getString("first_name"));
	            author.setLastName(resultSet.getString("last_name"));

	            // Set author to book
	            book.setAuthor(author);

	            result.add(book);
	        }
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(connection);
		}

		return result;
	}

	// complete this method
	@Override
	public List<Category> findAllCategories() {
		List<Category> result = new ArrayList<>();
		String sql = "select * from category";

		Connection connection = null;
		try {
			connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery();
	        while (resultSet.next()) {
	            Category category = new Category();
	            category.setId(resultSet.getLong("id"));
	            category.setCategoryDescription(resultSet.getString("category_description"));

	            result.add(category);
	        }
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return result;
	}

	
	
	@Override
	public List<Book> findBooksByCategory(String category) {
		List<Book> result = new ArrayList<Book>();
		 

		String sql = "SELECT book.id AS book_id, book.book_title, book.category_id, " +
                "author.first_name, author.last_name, category.category_description " +
                "FROM book " +
                "JOIN author ON book.id = author.book_id " +
                "JOIN category ON book.category_id = category.id " +
                "WHERE category.category_description = ?";

		Connection connection = null;
		try {
			connection = getConnection();
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setString(1, category);
	        ResultSet resultSet = statement.executeQuery();
	        while (resultSet.next()) {
	            Book book = new Book();
	            Author author = new Author();

	            // Populate Book object
	            book.setId(resultSet.getLong("book_id"));
	            book.setBookTitle(resultSet.getString("book_title"));
	            book.setCategoryId(resultSet.getLong("category_id"));
	            book.setCategory(resultSet.getString("category_description"));

	            // Populate Author object
	            author.setFirstName(resultSet.getString("first_name"));
	            author.setLastName(resultSet.getString("last_name"));

	            // Set author to book
	            book.setAuthor(author);

	            result.add(book);
	        }
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(connection);
		}
		return result;
	}
	
	@Override
	public void insert(Book book) {
		Connection connection = null;
		try {
			connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"insert into Book (book_title) values (?)",
					Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, book.getBookTitle());
			statement.execute();
			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				book.setId(generatedKeys.getLong(1));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(connection);
		}
	}

	@Override
	public void delete(Long bookId) {
		Connection connection = null;

		try {
			connection = getConnection();
			PreparedStatement statement = connection
					.prepareStatement("delete from book where id=?");
			statement.setLong(1, bookId);
			statement.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			closeConnection(connection);
		}
	}
	

}