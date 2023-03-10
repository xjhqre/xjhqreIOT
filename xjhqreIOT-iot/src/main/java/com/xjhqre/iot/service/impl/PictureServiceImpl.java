package com.xjhqre.iot.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.domain.dto.PictureDTO;
import com.xjhqre.common.domain.entity.Picture;
import com.xjhqre.common.exception.ServiceException;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.ImageUtil;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.common.utils.file.FileTypeUtils;
import com.xjhqre.common.utils.uuid.IdUtils;
import com.xjhqre.framework.utils.OSSUtil;
import com.xjhqre.framework.utils.OSSUtil.FileDirType;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.PictureService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.system.mapper.PictureMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * ArticleServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 10月 11, 2022
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    PictureMapper pictureMapper;
    @Resource
    ProductService productService;
    @Resource
    DeviceService deviceService;

    /**
     * 分页查询图片列表
     *
     * @param picture
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public IPage<Picture> find(Picture picture, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Picture> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(picture.getUploader() != null, Picture::getUploader, picture.getUploader());
        queryWrapper.eq(picture.getName() != null, Picture::getName, picture.getName());;
        return this.pictureMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 上传图片，管理员上传无需审核
     *
     * @param pictureDTO
     * @param mFile
     */
    @Override
    public Map<String, String> add(PictureDTO pictureDTO, MultipartFile mFile) {
        String extension = FileTypeUtils.getExtension(mFile.getOriginalFilename());

        if (!ImageUtil.SUFFIXS.contains(extension)) {
            throw new ServiceException("上传图片格式不支持！");
        }

        // 生成文件编号（唯一）
        String number = IdUtils.simpleUUID();
        pictureDTO.setNumber(number);
        pictureDTO.setName(mFile.getOriginalFilename());

        FileDirType fileDirType;
        Integer type = pictureDTO.getType();
        if (type == 1) {
            fileDirType = FileDirType.AVATAR;
        } else if (type == 2) {
            fileDirType = FileDirType.PRODUCT;
        } else if (type == 3) {
            fileDirType = FileDirType.SCREENSHOT;
        } else if (type == 4) {
            fileDirType = FileDirType.DEVICE;
        } else {
            fileDirType = FileDirType.PICTURE;
        }

        // 上传OSS
        String pictureUrl = OSSUtil.upload(mFile, fileDirType, number + extension);

        if (type == 1) {
        } else if (type == 2) {
            if (pictureDTO.getProductId() != null) {
                Product product = this.productService.getById(pictureDTO.getProductId());
                product.setImgUrl(pictureUrl);
                this.productService.update(product);
            }
        } else if (type == 3) {
        } else if (type == 4) {
            if (pictureDTO.getDeviceId() != null) {
                Device device = this.deviceService.getById(pictureDTO.getDeviceId());
                device.setImgUrl(pictureUrl);
                this.deviceService.update(device);
            }
        } else {
        }

        pictureDTO.setUrl(pictureUrl);
        pictureDTO.setUploader(SecurityUtils.getUserId());
        pictureDTO.setUploadTime(DateUtils.getNowDate());
        pictureDTO.setCreateTime(DateUtils.getNowDate());
        pictureDTO.setCreateBy(SecurityUtils.getUsername());

        // 存入数据库
        this.pictureMapper.insert(pictureDTO);

        Map<String, String> retmap = new HashMap<>();
        retmap.put("name", number);
        retmap.put("url", pictureUrl);

        return retmap;
    }

    /// **
    // * 审核图片
    // *
    // * @param pictureId
    // * @param result
    // * 审核结果 0：不通过 1：通过
    // */
    // @Override
    // public void audit(String pictureId, Integer result) {
    // Picture picture = this.pictureMapper.selectById(pictureId);
    // if (result == 1) {
    // // 传输图片的本地地址给 Python 程序，返回 OSS url地址
    // picture.setStatus(PictureConstant.PROCESSING);
    // this.pictureMapper.updateById(picture); // 处理过程较长，大约几秒，先保存状态
    //
    // // 发送到消息队列
    // this.rabbitMQSender.sendPictureProcessMessage(new String[] {pictureId});
    //
    // } else {
    // picture.setStatus(PictureConstant.FAILED);
    // this.pictureMapper.updateById(picture);
    // }
    // }

    /// **
    // * 批量审核图片
    // *
    // * @param pictureIds
    // * @param result
    // */
    // @Override
    // public void batchAudit(String[] pictureIds, Integer result) {
    // List<Picture> pictures = this.pictureMapper.selectBatchIds(Arrays.asList(pictureIds));
    // if (pictures.isEmpty()) {
    // throw new ServiceException("错误：获取图片为空");
    // }
    // if (result == 1) {
    // // 传输图片的本地地址给 Python 程序，返回 OSS url地址
    // for (Picture picture : pictures) {
    // picture.setStatus(PictureConstant.PROCESSING);
    // }
    // this.pictureMapper.updateBatchByIds(pictures);
    //
    // // 发送到消息队列
    // this.rabbitMQSender.sendPictureProcessMessage(pictureIds);
    //
    // } else {
    // for (Picture picture : pictures) {
    // picture.setStatus(PictureConstant.FAILED);
    // }
    // this.pictureMapper.updateBatchByIds(pictures);
    // }
    // }
}
