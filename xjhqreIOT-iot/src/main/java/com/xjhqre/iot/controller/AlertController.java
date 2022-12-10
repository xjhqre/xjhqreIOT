// package com.xjhqre.iot.controller;
//
// import java.util.List;
//
// import javax.servlet.http.HttpServletResponse;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.ruoyi.common.core.domain.AjaxResult;
// import com.ruoyi.common.core.page.TableDataInfo;
// import com.ruoyi.common.utils.poi.ExcelUtil;
// import com.ruoyi.iot.domain.Alert;
// import com.xjhqre.common.annotation.Log;
// import com.xjhqre.common.base.BaseController;
// import com.xjhqre.common.enums.BusinessType;
// import com.xjhqre.iot.service.AlertService;
//
/// **
// * 设备告警Controller
// *
// * @author kerwincui
// * @date 2022-01-13
// */
// @RestController
// @RequestMapping("/iot/alert")
// public class AlertController extends BaseController {
// @Autowired
// private AlertService alertService;
//
// /**
// * 查询设备告警列表
// */
// @PreAuthorize("@ss.hasPermi('iot:alert:list')")
// @GetMapping("/list")
// public TableDataInfo list(Alert alert) {
// startPage();
// List<Alert> list = this.alertService.selectAlertList(alert);
// return getDataTable(list);
// }
//
// /**
// * 导出设备告警列表
// */
// @PreAuthorize("@ss.hasPermi('iot:alert:export')")
// @Log(title = "设备告警", businessType = BusinessType.EXPORT)
// @PostMapping("/export")
// public void export(HttpServletResponse response, Alert alert) {
// List<Alert> list = this.alertService.selectAlertList(alert);
// ExcelUtil<Alert> util = new ExcelUtil<Alert>(Alert.class);
// util.exportExcel(response, list, "设备告警数据");
// }
//
// /**
// * 获取设备告警详细信息
// */
// @PreAuthorize("@ss.hasPermi('iot:alert:query')")
// @GetMapping(value = "/{alertId}")
// public AjaxResult getInfo(@PathVariable("alertId") Long alertId) {
// return AjaxResult.success(this.alertService.selectAlertByAlertId(alertId));
// }
//
// /**
// * 新增设备告警
// */
// @PreAuthorize("@ss.hasPermi('iot:alert:add')")
// @Log(title = "设备告警", businessType = BusinessType.INSERT)
// @PostMapping
// public AjaxResult add(@RequestBody Alert alert) {
// return toAjax(this.alertService.insertAlert(alert));
// }
//
// /**
// * 修改设备告警
// */
// @PreAuthorize("@ss.hasPermi('iot:alert:edit')")
// @Log(title = "设备告警", businessType = BusinessType.UPDATE)
// @PutMapping
// public AjaxResult edit(@RequestBody Alert alert) {
// return toAjax(this.alertService.updateAlert(alert));
// }
//
// /**
// * 删除设备告警
// */
// @PreAuthorize("@ss.hasPermi('iot:alert:remove')")
// @Log(title = "设备告警", businessType = BusinessType.DELETE)
// @DeleteMapping("/{alertIds}")
// public AjaxResult remove(@PathVariable Long[] alertIds) {
// return toAjax(this.alertService.deleteAlertByAlertIds(alertIds));
// }
// }
