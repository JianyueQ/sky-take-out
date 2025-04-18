package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkspaceService workspaceService;



    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new  ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = reportMapper.sumByMap(map);
            turnover = turnover != null ? turnover : 0.0;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new  ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }


        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map mapUser = new HashMap();
            mapUser.put("end", endTime);
            //总用户
            Integer totalUsers = reportMapper.sumByUserMap(mapUser);
            totalUserList.add(totalUsers);
            mapUser.put("begin", beginTime);
            //新增用户数量
            Integer NewUsers = reportMapper.sumByUserMap(mapUser);
            NewUsers = NewUsers != null ? NewUsers : 0;
            newUserList.add(NewUsers);
        }


        return UserReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> localDateList = LocalDateList(begin, end);

        List<Integer> dailyOrderCountList = new ArrayList<>();
        List<Integer> dailyValidOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;


        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            //每日订单数
            Map orderMap = new HashMap();
            orderMap.put("begin", beginTime);
            orderMap.put("end", endTime);
            Integer dailyOrderCount = reportMapper.sumByOrdersMap(orderMap);
            dailyOrderCountList.add(dailyOrderCount);
            //订单总数
            totalOrderCount += dailyOrderCount;
            //每日有效订单数
            orderMap.put("status", Orders.COMPLETED);
            Integer dailyValidOrderCount = reportMapper.sumByOrdersMap(orderMap);
            dailyValidOrderCountList.add(dailyValidOrderCount);
            //有效订单数
            validOrderCount += dailyValidOrderCount;

        }
        // 计算订单完成率（保留2位小数） 订单完成率% = 有效订单  /  订单总数
//        Double orderCompletionRate = totalOrderCount > 0 ?
//                Double.parseDouble(String.format("%.2f", validOrderCount * 100.0 / totalOrderCount)) : 0.0;
        Double orderCompletionRate = totalOrderCount > 0 ? (double) validOrderCount / totalOrderCount : 0.0;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(localDateList, ","))
                .orderCountList(StringUtils.join(dailyOrderCountList, ","))
                .validOrderCountList(StringUtils.join(dailyValidOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询销量排名top10接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        Map mapTopTen = new HashMap();
        mapTopTen.put("begin", beginTime);
        mapTopTen.put("end", endTime);
        mapTopTen.put("status", Orders.COMPLETED);

        List<GoodsSalesDTO> goodsSalesDTOList = reportMapper.topTenByMap(mapTopTen);

        List<String> stringNameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> stringNumberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String nameList = StringUtils.join(stringNameList, ",");
        String numberList = StringUtils.join(stringNumberList, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出Excel报表接口
     * @param response
     */
    @Override
    public void exportExcelFile(HttpServletResponse response) {
        //查询数据库,获取营业数据(最近30天)
        LocalDate begin = LocalDate.now().minusDays(31);
        LocalDate end = LocalDate.now().minusDays(1);

        //查询数据
        BusinessDataVO businessData = getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //通过POI写入模板文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(in);
            // 获取或创建单元格样式
            XSSFCellStyle centerStyle = xssfWorkbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            //获取Sheet页
            XSSFSheet sheet = xssfWorkbook.getSheet("Sheet1");
            // 设置单元格值和样式
            XSSFCell cell = sheet.getRow(1).getCell(1);
            cell.setCellValue("时间"+begin+"至"+end);
            cell.setCellStyle(centerStyle);
            //填充数据
//            sheet.getRow(1).getCell(1).setCellValue("时间"+begin+"至"+end);

            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());

            for (int i = 0; i < 30; i++) {
                begin = begin.plusDays(1);
                BusinessDataVO data = getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(begin, LocalTime.MAX));
                sheet.getRow(7+i).getCell(1).setCellValue(String.valueOf(begin));
                sheet.getRow(7+i).getCell(2).setCellValue(data.getTurnover() != null ? data.getTurnover() : 0.0);
                sheet.getRow(7+i).getCell(3).setCellValue(data.getValidOrderCount() != null ? data.getValidOrderCount() : 0.0);
                sheet.getRow(7+i).getCell(4).setCellValue(data.getOrderCompletionRate() != null ? data.getOrderCompletionRate() : 0.0);
                sheet.getRow(7+i).getCell(5).setCellValue(data.getUnitPrice() != null ? data.getUnitPrice() : 0.0);
                sheet.getRow(7+i).getCell(6).setCellValue(data.getNewUsers() != null ? data.getNewUsers() : 0.0);
            }

            //通过输出流将文件下载到客户端
            ServletOutputStream stream = response.getOutputStream();
            xssfWorkbook.write(stream);
            //关闭资源
            stream.close();
            xssfWorkbook.close();

        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 计算日期
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> LocalDateList(LocalDate begin, LocalDate end){
        List<LocalDate> localDateList = new  ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }
        return localDateList;
    }

    /**
     * 计算营业数据
     * @param begin
     * @param end
     * @return
     */
    private BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        //获取新增用户数量
        Integer sumUser = reportMapper.sumByUserMap(map);
        //获取订单完成率
        Integer sumOrders = reportMapper.sumByOrdersMap(map); //今日订单总数
        map.put("status", Orders.COMPLETED);
        //有效订单数量
        Integer sumValidOrders = reportMapper.sumByOrdersMap(map);
        //订单完成率
        Double OrderFulfillmentRate = sumOrders > 0 ? sumValidOrders / sumOrders : 0.0;
        //计算营业额
        Double todayTurnover = reportMapper.sumByMap(map);
        //计算平均客单价
        Double todayAverageOrderValue = sumValidOrders > 0 ? todayTurnover / sumValidOrders : 0.0;
        return BusinessDataVO.builder()
                .turnover(todayTurnover)
                .validOrderCount(sumValidOrders)
                .orderCompletionRate(OrderFulfillmentRate)
                .unitPrice(todayAverageOrderValue)
                .newUsers(sumUser)
                .build();
    }
}
