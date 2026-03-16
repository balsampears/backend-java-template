package com.balsam.system.common.core.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.balsam.system.common.constant.HttpStatus;
import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author ruoyi
 */
public class PageDataInfo<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> rows;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    /**
     * 表格数据对象
     */
    public PageDataInfo()
    {
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public PageDataInfo(List<T> list, long total)
    {
        this.rows = list;
        this.total = total;
        this.code = HttpStatus.SUCCESS;
        this.msg = "查询成功";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static<T> PageDataInfo<T> getDataTable(List<T> list)
    {
        PageDataInfo rspData = new PageDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    public static<T> PageDataInfo<T> getDataTable(IPage<T> page)
    {
        PageDataInfo<T> rspData = new PageDataInfo<>();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(page.getRecords());
        rspData.setMsg("查询成功");
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    public PageDataInfo(List<T> list)
    {
        this.setCode(HttpStatus.SUCCESS);
        this.setRows(list);
        this.setMsg("查询成功");
        this.setTotal(new PageInfo<>(list).getTotal());
    }

    public PageDataInfo(IPage<T> page)
    {
        this.setCode(HttpStatus.SUCCESS);
        this.setRows(page.getRecords());
        this.setMsg("查询成功");
        this.setTotal(page.getTotal());
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<T> getRows()
    {
        return rows;
    }

    public void setRows(List<T> rows)
    {
        this.rows = rows;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}