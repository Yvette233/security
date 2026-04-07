package com.pojo;

import com.common.BasicModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity()
@Table(name = "SYS_POSITION")
public class SysPosition extends BasicModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "id", strategy = "assigned")
    @Column(name = "ID", length = 50)
    private String id;

    @Column(name = "NAME", length = 50)
    private String name;

    @Column(name = "TYPE", length = 50)
    private String type;

    // 父职位
    @Column(name = "PARENT_ID", length = 50)
    private String parentId;

    @Column(name = "HIERARCHY")
    private Integer hierarchy;

    //many-to-one
    @Transient
    private SysPosition parent;

    // 子部门
    @Transient
    private List<SysPosition> children;

    /***********************************************************************/

    public SysPosition getParent() {
        return parent;
    }

    public void setParent(SysPosition parent) {
        this.parent = parent;
    }

    public List<SysPosition> getChildren() {
        return children;
    }

    public void setChildren(List<SysPosition> children) {
        this.children = children;
    }

    /***********************************************************************/

    @Override
    public String getUuid() {
        // TODO Auto-generated method stub
        return this.getId();
    }

    @Override
    public void setUuid(String uuid) {
        // TODO Auto-generated method stub
        this.setId(uuid);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void destroy() {
        // TODO Auto-generated method stub
        try {
//			if (this.childDepts != null) {
//				this.childDepts.clear();
//				this.childDepts = null;
//			}
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Integer getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Integer hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}