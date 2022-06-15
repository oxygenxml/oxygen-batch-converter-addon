/*
 * Copyright (c) 2022 Syncro Soft SRL - All Rights Reserved.
 *
 * This file contains proprietary and confidential source code.
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 */

package com.oxygenxml.resources.batch.converter.utils;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that holds utility methods used when HiDPI is detected.
 * 
 * @author gabriel_nedianu
 *
 */
public class HiDPIUtil {

  /**
   * Constructor
   */
  private HiDPIUtil() {/*Not implemented*/}

  /**
   * Logger for logging.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(HiDPIUtil.class.getName());

  /**
   * Applies a scaling factor depending if we are on a hidpi display.
   * 
   * @param dimension to scale.
   * 
   * @return A scaled dimension.
   */
  public static int getHiDPISize(int dimension) {
    float scalingFactor = (float) 1.0;
    if (HiDPIUtil.isRetinaNoImplicitSupport()) {
      scalingFactor = HiDPIUtil.getScalingFactor();
    }

    return (int) (scalingFactor * dimension);
  }

  /**
   * @return <code>true</code> if it is the Windows style Retina, where the application must explicitly change its font and use double sized icons. 
   */
  private static boolean isRetinaNoImplicitSupport() {
    boolean isRetinaNoImplicitSupport = false;
    try {
      Class<?> retinaDetectorClass = Class.forName("ro.sync.ui.hidpi.RetinaDetector");
      Method getInstanceMethod = retinaDetectorClass.getMethod("getInstance");
      Object retinaDetectorObj = getInstanceMethod.invoke(null);
      Method isRetinaNoImplicitSupportMethod = retinaDetectorClass.getMethod("isRetinaNoImplicitSupport");
      isRetinaNoImplicitSupport = (boolean) isRetinaNoImplicitSupportMethod.invoke(retinaDetectorObj);
    } catch (Exception e) {
      LOGGER.debug(e.getMessage(), e);
    }
    return isRetinaNoImplicitSupport;
  }

  /**
   * On Mac OS X with retina display this factor is 2.
   * 
   * @return Returns the HiDPI scaling factor, or 1 if there is no scaling. 
   */
  private static float getScalingFactor() {
    float scalingFactor = 1.0f;
    try {
      Class<?> retinaDetectorClass = Class.forName("ro.sync.ui.hidpi.RetinaDetector");
      Method getInstanceMethod = retinaDetectorClass.getMethod("getInstance");
      Object retinaDetectorObj = getInstanceMethod.invoke(null);
      Method getScalingFactorMethod = retinaDetectorClass.getMethod("getScalingFactor");
      scalingFactor = (float) getScalingFactorMethod.invoke(retinaDetectorObj);
    } catch (Exception e) {
      LOGGER.debug(e.getMessage(), e);
    }
    return scalingFactor;
  }

}
