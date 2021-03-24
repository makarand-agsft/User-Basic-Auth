package com.formz.model;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role extends AuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long roleId;

	@Column(name = "role")
	private String role;

	public Role(String role) {
		this.role = role;
	}

	Role(){

	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
    public String toString() {
		return "Role{" + "roleId=" + roleId + ", role='" + role + '\'' + '}';
	}
}
