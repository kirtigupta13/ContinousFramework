package com.cerner.devcenter.education.utils;

/***
 * Contains the many sql queries that are used within the project.
 */
public enum SqlQueries {

  // for AddUserDAO
  INSERT_USER_QUERY("INSERT INTO public.admins (user_id, first_name, last_name, email_id, role, auth_level) VALUES (?,?,?,?,?,?);"),
  CHECK_ADMIN("SELECT user_id,first_name, last_name, email_id, role from public.admins where user_id = ?"),
  CHECK_DUPLICATE_DATA("SELECT user_id FROM public.admins WHERE user_id = ? "),
  DELETE_USER("DELETE FROM public.admins WHERE user_id=?"),
  SELECT_USERS("SELECT user_id, first_name, last_name, email_id, role, auth_level FROM public.admins"),
  SELECT_USER_BY_USERID("SELECT user_id FROM public.admins WHERE UPPER (user_id) = UPPER(?)"),
  SELECT_USERS_BY_AUTHORIZATION_LEVEL("SELECT user_id, first_name, last_name, email_id, role, auth_level FROM public.admins WHERE auth_level = ?"),
  UPDATE_ADMIN_USER_BY_USERID("UPDATE public.admins set role = ?,auth_level = ? WHERE user_id = ?"),
  REVOKE_ADMIN_BY_USERID("UPDATE public.admins set role = ?,auth_level = ? WHERE user_id = ?"),
  CHANGE_AUTH_LEVEL_BASED_ON_USER_ID("update public.admins set role = ?, auth_level =? where user_id = ?"),
  // for CourseDAO
  SELECT_CATEGORY_NAMES("SELECT * FROM category WHERE Lower(name) LIKE Lower(?)"),
  GET_ALL_COURSES_QUERY("SELECT COURSE_ID, NAME, URL, INSTRUCTOR, DIFFICULTY_LEVEL FROM COURSE"),
  GET_COURSE_BY_ID_QUERY("SELECT COURSE_ID, NAME, URL, INSTRUCTOR, DIFFICULTY_LEVEL FROM COURSE WHERE COURSE_ID=?"),
  GET_COURSE_BY_CATEGORY_QUERY("SELECT C.COURSE_ID, C.NAME, C.URL, C.INSTRUCTOR, C.DIFFICULTY_LEVEL FROM COURSE C, "
                + "CATEGORY_COURSE_RELTN CCR WHERE C.COURSE_ID = CCR.COURSE_ID AND CATEGORY_ID = ?"),
  INSERT_COURSE_QUERY("INSERT INTO COURSE (NAME, URL, INSTRUCTOR, DIFFICULTY_LEVEL) VALUES(?,?,?,?)"),
  GET_COURSE_BY_NAME_QUERY("SELECT COURSE_ID FROM COURSE WHERE NAME=?"),
  GET_COURSE_DESCRIPTION_BY_ID_QUERY("SELECT description FROM course WHERE course_id = ?"),
  DELETE_COURSE_QUERY("DELETE FROM COURSE WHERE COURSE_ID=?"),
  //for ResourceType DAO
  GET_RESOURCE_TYPE_NAME_BY_TYPE_ID("SELECT type_name FROM type WHERE type_id = ?");
  private final String query;

  /**
   * Constructor
   * 
   * @param query
   */
  private SqlQueries(String query) {
    this.query = String.valueOf(query);
  }

  /**
   * Returns the SQL Query associated with object
   * 
   * @return query
   */
  public String getQuery() {
    return this.query;
  }
}
