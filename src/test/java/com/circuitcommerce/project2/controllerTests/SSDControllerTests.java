package com.circuitcommerce.project2.controllerTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.circuitcommerce.project2.dto.ProductShortDto;

import java.util.ArrayList;

import com.circuitcommerce.project2.controller.SolidStateDriveController;
import com.circuitcommerce.project2.dto.SolidStateDriveDto;
import com.circuitcommerce.project2.model.SolidStateDrive;
import com.circuitcommerce.project2.service.SolidStateDriveService;

@SpringBootTest
public class SSDControllerTests {
private MockMvc mock;

  @Mock
  private SolidStateDriveService ssdServ;
  @InjectMocks
  private SolidStateDriveController ssdCont;
  private SolidStateDrive ssd;
  private SolidStateDriveDto ssdDto;
  private ArrayList<SolidStateDrive> listfound;
  private ArrayList<ProductShortDto> listEmpty;
  private ArrayList<ProductShortDto> listofpos;

  private String maxSeqRead = "Up to 560 MBps";
  private String maxSeqWrite = "Up to 460 MBps";
  private String usedFor = "";
  private String mttf = "1,500,000 hours";
  private String kbRandomRead = "Up to 74,000 IOPS";
  private String kb4RandomWrite = "Up to 76,000 IOPS";
  private String controller = "";

  private String brand = "Mushkin Enhanced";
  private Long productId = (long) 12345;
  private String modelNumber = "MKNSSDRE1TB";
  private String title = "Mushkin Enhanced Reactor 2.5\" 1TB SATA III MLC Internal Solid State Drive (SSD) MKNSSDRE1TB";
  private String description = "My(Matthew Sheldon) Hard Drive in my Computer. It's really fast. Would be faster if I attached it to the correct SATA but too lazy.";
  private Double price = 209.99;
  private ProductShortDto pos = new ProductShortDto(productId, title, modelNumber, price, null);

  @BeforeEach
  public void before() {
    listfound = new ArrayList<>();
    listEmpty = new ArrayList<>();
    listofpos = new ArrayList<>();
    ssd = new SolidStateDrive(maxSeqRead, maxSeqWrite, usedFor, mttf, kbRandomRead, kb4RandomWrite, controller);
    ssdDto = new SolidStateDriveDto(maxSeqRead, maxSeqWrite, usedFor, mttf, kbRandomRead, kb4RandomWrite, controller);
    ssd.setBrand(brand);
    ssd.setProductId(productId);
    ssd.setModelNumber(modelNumber);
    ssd.setTitle(title);
    ssd.setPrice(price);
    ssdDto.setBrand(brand);
    ssdDto.setModelNumber(modelNumber);
    ssdDto.setTitle(title);
    ssdDto.setPrice(price);
    listfound.add(ssd);
    listofpos.add(pos);
    mock=MockMvcBuilders.standaloneSetup(ssdCont).build();
    doNothing().when(ssdServ).insertSSD(ssdDto);
    when(ssdServ.getSolidStateDriveById(productId)).thenReturn(ssd);
    when(ssdServ.getAllSolidStateDrives()).thenReturn(listofpos);
    when(ssdServ.findByBrand(brand)).thenReturn(listofpos);
    when(ssdServ.findBetweenPrice(150.0, 300.0)).thenReturn(listofpos);

  }

  @Test
  public void notNullTest() throws Exception {
    assertThat(ssdCont).isNotNull();
  }

  @Test
  public void getSSDByIdTest1() throws Exception {
    mock.perform(
      get("/ssd/id/{id}",ssd.getProductId())).
      andExpect(status().isOk()).
      andExpect(jsonPath("$.maxReadSeq", is(ssd.getMaxSeqRead())));
  }
  
}
