declare var process: any;

const backendHost = window.__petclinic__backend_host__.replace(
  "__BACKEND__",
  "http://localhost:9977"
);
export const graphqlApiUrl = `${backendHost}/graphql`;

console.log("USING GRAPHQL API URL", graphqlApiUrl);
