// package com.xjhqre.iot.controller;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;
//
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.xjhqre.common.base.BaseController;
// import com.xjhqre.common.domain.R;
// import com.xjhqre.common.domain.entity.Picture;
// import com.xjhqre.common.exception.ServiceException;
// import com.xjhqre.framework.service.PictureService;
//
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiImplicitParam;
// import io.swagger.annotations.ApiImplicitParams;
// import io.swagger.annotations.ApiOperation;
//
/// **
// * <p>
// * PictureController
// * </p>
// *
// * @author xjhqre
// * @since 10月 25, 2022
// */
// @Api(value = "图片操作接口", tags = "图片操作接口")
// @RestController
// @RequestMapping("/admin/picture")
// public class PictureController extends BaseController {
//
// @Autowired
// PictureService pictureService;
//
// @ApiOperation(value = "分页查询图片列表")
// @ApiImplicitParams({
// @ApiImplicitParam(name = "pageNum", value = "正整数，表示查询第几页", required = true, dataType = "int", example = "1"),
// @ApiImplicitParam(name = "pageSize", value = "正整数，表示每页几条记录", required = true, dataType = "int",
// example = "20")})
// @GetMapping("find/{pageNum}/{pageSize}")
// public R<IPage<Picture>> find(Picture picture, @PathVariable("pageNum") Integer pageNum,
// @PathVariable("pageSize") Integer pageSize) {
// return R.success(this.pictureService.findPicture(picture, pageNum, pageSize));
// }
//
// @ApiOperation(value = "上传图片")
// @PostMapping(value = "/add")
// public R<String> add(@Validated Picture picture, @RequestParam("file") MultipartFile mFile) {
// if (mFile == null) {
// throw new ServiceException("上传图片文件为空");
// }
// this.pictureService.savePicture(picture, mFile);
// return R.success("上传图片成功");
// }
//
// // @ApiOperation(value = "审核图片")
// // @PostMapping(value = "/audit/{pictureId}/{result}")
// // public R<String> audit(@PathVariable String pictureId, @PathVariable Integer result) {
// //
// // this.pictureService.audit(pictureId, result);
// // return R.success("审核成功");
// // }
//
// @ApiOperation(value = "审核图片")
// @PostMapping(value = "/batchAudit/{result}")
// public R<String> batchAudit(@RequestBody String[] pictureIds, @PathVariable Integer result) {
//
// this.pictureService.batchAudit(pictureIds, result);
// return R.success("批量审核成功");
// }
//
// }
