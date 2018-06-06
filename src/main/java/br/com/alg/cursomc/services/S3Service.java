package br.com.alg.cursomc.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import br.com.alg.cursomc.services.exceptions.FileException;

@Service
public class S3Service {

	private Logger LOG = LoggerFactory.getLogger(S3Service.class);
	
	@Value("${s3.bucket}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 s3Client;
	
	public URI uploadFile(MultipartFile multipartFile) {
		try {
			LOG.info("Iniciando Upload");
			String fileName = multipartFile.getOriginalFilename();
			InputStream is = multipartFile.getInputStream();
			String contentType = multipartFile.getContentType();
			return uploadFile(is, fileName, contentType);
		} catch (IOException e) {
			throw new FileException("Erro de IO " + e.getMessage());
		}	
	}
	
	public URI uploadFile(InputStream is, String fileName, String contentType) {
		
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(contentType);
		LOG.info("Upload iniciado");
		s3Client.putObject(new PutObjectRequest(bucketName, fileName, is, meta));
		LOG.info("Upload finalizado");
		
		try {
			return s3Client.getUrl(bucketName, fileName).toURI();
		} catch (URISyntaxException e) {
			throw new FileException("Erro ao converte URL para URI");
		}
	}
	
}
