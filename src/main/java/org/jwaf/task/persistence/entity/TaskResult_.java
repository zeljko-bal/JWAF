package org.jwaf.task.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-11-23T18:29:53.484+0100")
@StaticMetamodel(TaskResult.class)
public class TaskResult_ {
	public static volatile SingularAttribute<TaskResult, Integer> id;
	public static volatile SingularAttribute<TaskResult, String> employee;
	public static volatile SingularAttribute<TaskResult, String> employer;
	public static volatile SingularAttribute<TaskResult, String> taskType;
	public static volatile SingularAttribute<TaskResult, String> content;
}
