package com.gkmonk.pos.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ImageDBServiceImpl {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> saveImages(List<byte[]> images){

        List<String> imageIds = new ArrayList<>();
        for(byte[] image : images) {
            InputStream inputStream = new ByteArrayInputStream(image);
            String imageId = saveImages(inputStream,"sample.jpg");
            imageIds.add(imageId);
        }
        return imageIds;
    }

    public String saveImages(InputStream inputStream,String filename) {
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document("type", "file")); // optional metadata
        GridFSBucket gridFSBucket = getGridFSBucket();
        ObjectId fileId = gridFSBucket.uploadFromStream(filename, inputStream, options);
        System.out.println("File ID: " + fileId.toHexString());
        return fileId.toHexString();
    }

    public GridFSBucket getGridFSBucket() {
        MongoDatabase database = mongoClient.getDatabase("pos_system");
        return  GridFSBuckets.create(database, "productImages");
    }

    public List<byte[]> fetchInventoryImagesById(String ids) {
        List<byte[]> images = new ArrayList<>();
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document("type", "file")); // optional metadata
        GridFSBucket gridFSBucket = getGridFSBucket();
        String[] imageId = ids.split("[,]");
        for(String id : imageId) {
            gridFSBucket.find(new Document("_id", new ObjectId(id))).forEach(new Consumer<GridFSFile>() {
                @Override
                public void accept(final GridFSFile gridFSFile) {
                    if (gridFSFile != null) {
                        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        try {
                            StreamUtils.copy(downloadStream, outputStream);
                            images.add(outputStream.toByteArray());
                            downloadStream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });
        }
        return images;
    }
}
