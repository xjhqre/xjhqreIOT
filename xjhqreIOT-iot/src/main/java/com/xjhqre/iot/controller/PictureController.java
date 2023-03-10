package com.xjhqre.iot.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.common.base.BaseController;
import com.xjhqre.common.domain.R;
import com.xjhqre.common.domain.dto.PictureDTO;
import com.xjhqre.common.domain.entity.Picture;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.iot.service.PictureService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * PictureController
 * </p>
 *
 * @author xjhqre
 * @since 10月 25, 2022
 */
@Api(value = "图片操作接口", tags = "图片操作接口")
@RestController
@RequestMapping("/iot/picture")
public class PictureController extends BaseController {

    @Resource
    PictureService pictureService;

    @ApiOperation(value = "分页查询图片列表")
    @GetMapping("/find")
    public R<IPage<Picture>> find(Picture picture, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return R.success(this.pictureService.find(picture, pageNum, pageSize));
    }

    @ApiOperation(value = "上传图片")
    @PostMapping(value = "/add")
    public R<Map<String, String>> add(PictureDTO pictureDTO, @RequestParam("file") MultipartFile mFile) {
        if (mFile == null) {
            throw new ServiceException("上传图片文件为空");
        }
        return R.success(this.pictureService.add(pictureDTO, mFile));
    }

    // @ApiOperation(value = "审核图片")
    // @PostMapping(value = "/audit/{pictureId}/{result}")
    // public R<String> audit(@PathVariable String pictureId, @PathVariable Integer result) {
    //
    // this.pictureService.audit(pictureId, result);
    // return R.success("审核成功");
    // }

    // @ApiOperation(value = "审核图片")
    // @PostMapping(value = "/batchAudit/{result}")
    // public R<String> batchAudit(@RequestBody String[] pictureIds, @PathVariable Integer result) {
    //
    // this.pictureService.batchAudit(pictureIds, result);
    // return R.success("批量审核成功");
    // }

}
