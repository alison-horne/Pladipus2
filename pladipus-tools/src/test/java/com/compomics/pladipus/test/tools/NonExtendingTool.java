package com.compomics.pladipus.test.tools;

import com.compomics.pladipus.tools.annotations.PladipusTool;

/**
 * Blank test class to check that classes annotated with @PladipusTool but which do not extend PladipusToolBase are not registered.
 *
 */
@PladipusTool(displayName = "Not PladipusBase")
public class NonExtendingTool {

}
