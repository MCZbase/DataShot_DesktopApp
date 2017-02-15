/**
 * 
 */
package edu.harvard.mcz.imagecapture.tests;

/**
 * Used to allow an integration test profile to overwrite the default excludedGroups value.
 * If used as a Category annotation on any test class, will result in that test being 
 * skipped in when the integration test profile is used. 
 * 
 * @author mole
 *
 */
public interface IgnoreInIntegration { }
